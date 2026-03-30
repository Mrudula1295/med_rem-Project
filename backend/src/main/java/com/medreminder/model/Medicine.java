package com.medreminder.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "medicines")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String imagePath; // path to the uploaded image

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // AI Specific Fields
    private Boolean aiSafe;
    private String aiDosage;
    private String aiTiming;
    
    @Column(columnDefinition = "TEXT")
    private String aiWarning;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Reminder> reminders;
}
