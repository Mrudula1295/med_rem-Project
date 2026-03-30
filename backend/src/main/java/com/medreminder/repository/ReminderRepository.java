package com.medreminder.repository;

import com.medreminder.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    @Query("SELECT r FROM Reminder r JOIN FETCH r.medicine m WHERE m.user.id = :userId")
    List<Reminder> findByMedicineUserId(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Reminder r JOIN FETCH r.medicine m WHERE r.nextReminderTime <= :now AND r.status IN ('PENDING', 'SNOOZED')")
    List<Reminder> findDueReminders(@Param("now") LocalDateTime now);
    
    @Query("SELECT r FROM Reminder r JOIN FETCH r.medicine m WHERE m.id = :medicineId")
    List<Reminder> findByMedicineId(@Param("medicineId") Long medicineId);
}
