package com.medreminder.service;

import com.medreminder.model.Reminder;
import com.medreminder.model.User;
import com.medreminder.repository.ReminderRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderScheduler {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired(required = false)
    private JavaMailSender emailSender;

    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private boolean isTwilioInitialized = false;

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> dueReminders = reminderRepository.findDueReminders(now);
        
        for (Reminder reminder : dueReminders) {
            System.out.println("Processing reminder targeted at: " + reminder.getNextReminderTime());
            
            try {
                if (emailSender != null) {
                    User user = reminder.getMedicine().getUser();
                    String reminderMessage = "Hello " + user.getUsername() + ",\n\nIt is time to take your medicine: " + reminder.getMedicine().getName() + ".\n\nPlease log into MedReminder to mark it as taken.";

                    // 1. Send Email
                    if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(user.getEmail());
                        message.setSubject("Medicine Reminder: " + reminder.getMedicine().getName());
                        message.setText(reminderMessage);
                        emailSender.send(message);
                        System.out.println("Email reminder sent to " + user.getEmail());
                    }

                    // 2. Send SMS via Twilio
                    if (user.getMobile() != null && !user.getMobile().isEmpty()) {
                        if (!isTwilioInitialized && !twilioAccountSid.startsWith("YOUR_")) {
                            Twilio.init(twilioAccountSid, twilioAuthToken);
                            isTwilioInitialized = true;
                        }
                        
                        if (isTwilioInitialized) {
                            Message sms = Message.creator(
                                    new PhoneNumber(user.getMobile()), // To
                                    new PhoneNumber(twilioPhoneNumber), // From
                                    reminderMessage // Body
                            ).create();
                            System.out.println("SMS reminder sent to " + user.getMobile() + ", SID: " + sms.getSid());
                        } else {
                            System.out.println("Twilio not configured properly. SMS skipped for " + user.getMobile());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to send reminder notification: " + e.getMessage());
            }

            // Advance nextReminderTime by 5 minutes for persistence
            reminder.setStatus("SNOOZED");
            reminder.setNextReminderTime(now.plusMinutes(5));
            reminderRepository.save(reminder);
        }
    }
}
