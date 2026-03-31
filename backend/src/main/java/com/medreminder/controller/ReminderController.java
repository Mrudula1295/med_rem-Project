package com.medreminder.controller;

import com.medreminder.model.Reminder;
import com.medreminder.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin("*")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @PostMapping("/set")
    public ResponseEntity<?> setReminder(@NonNull @RequestParam("medicineId") Long medicineId,
                                         @RequestParam("reminderTime") String reminderTimeStr,
                                         @RequestParam(value = "repeatMode", defaultValue = "NONE") String repeatMode) {
        try {
            // Support both YYYY-MM-DDTHH:MM:SS and YYYY-MM-DDTHH:MM
            String formattedTime = reminderTimeStr.length() == 16 ? reminderTimeStr + ":00" : reminderTimeStr;
            LocalDateTime reminderTime = LocalDateTime.parse(formattedTime);
            Reminder reminder = reminderService.setReminder(medicineId, reminderTime, repeatMode);
            return ResponseEntity.ok(reminder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reminder>> getUserReminders(@NonNull @PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.getUserReminders(userId));
    }

    @PostMapping("/mark-taken/{reminderId}")
    public ResponseEntity<?> markAsTaken(@NonNull @PathVariable Long reminderId) {
        try {
            Reminder reminder = reminderService.markAsTaken(reminderId);
            return ResponseEntity.ok(reminder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/snooze/{reminderId}")
    public ResponseEntity<?> snoozeReminder(@NonNull @PathVariable Long reminderId) {
        try {
            Reminder reminder = reminderService.snoozeReminder(reminderId);
            return ResponseEntity.ok(reminder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{reminderId}")
    public ResponseEntity<?> deleteReminder(@NonNull @PathVariable Long reminderId) {
        try {
            reminderService.deleteReminder(reminderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
