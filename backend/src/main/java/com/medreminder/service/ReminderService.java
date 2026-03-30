package com.medreminder.service;

import com.medreminder.model.Medicine;
import com.medreminder.model.Reminder;
import com.medreminder.repository.MedicineRepository;
import com.medreminder.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    public Reminder setReminder(@NonNull Long medicineId, LocalDateTime reminderTime, String repeatMode) {
        Medicine medicine = medicineRepository.findById(medicineId)
            .orElseThrow(() -> new RuntimeException("Medicine not found"));
            
        Reminder reminder = new Reminder();
        reminder.setMedicine(medicine);
        reminder.setReminderTime(reminderTime);
        reminder.setStatus("PENDING");
        reminder.setRepeatMode(repeatMode != null ? repeatMode : "NONE");
        reminder.setNextReminderTime(reminderTime);
        return reminderRepository.save(reminder);
    }

    public List<Reminder> getUserReminders(Long userId) {
        return reminderRepository.findByMedicineUserId(userId);
    }
    
    public List<Reminder> getRemindersForMedicine(Long medicineId) {
        return reminderRepository.findByMedicineId(medicineId);
    }

    public Reminder markAsTaken(@NonNull Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new RuntimeException("Reminder not found"));
        
        if ("DAILY".equalsIgnoreCase(reminder.getRepeatMode())) {
            reminder.setStatus("PENDING");
            reminder.setNextReminderTime(reminder.getReminderTime().plusDays(1)); // Reschedule for next day
            reminder.setReminderTime(reminder.getReminderTime().plusDays(1));
        } else {
            reminder.setStatus("TAKEN");
        }
        return reminderRepository.save(reminder);
    }
    
    public Reminder snoozeReminder(@NonNull Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new RuntimeException("Reminder not found"));
        reminder.setStatus("SNOOZED");
        reminder.setNextReminderTime(LocalDateTime.now().plusMinutes(10)); // 10 minutes snooze request
        return reminderRepository.save(reminder);
    }

    public void deleteReminder(@NonNull Long reminderId) {
        reminderRepository.deleteById(reminderId);
    }
}
