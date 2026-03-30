package com.medreminder.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "reminders")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private LocalDateTime reminderTime;

    // Status: PENDING, TAKEN, IGNORED, SNOOZED
    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDateTime nextReminderTime;

    // NONE, DAILY
    @Column(nullable = false)
    private String repeatMode = "NONE";
}
