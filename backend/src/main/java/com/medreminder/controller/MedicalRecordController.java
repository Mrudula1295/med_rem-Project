package com.medreminder.controller;

import com.medreminder.model.MedicalRecord;
import com.medreminder.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@CrossOrigin("*")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadRecord(
            @NonNull @RequestParam("userId") Long userId,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file) {
        try {
            MedicalRecord record = medicalRecordService.uploadRecord(userId, title, file);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<MedicalRecord>> getRecords(@NonNull @PathVariable Long userId) {
        return ResponseEntity.ok(medicalRecordService.getUserRecords(userId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRecord(@NonNull @PathVariable Long id) {
        try {
            medicalRecordService.deleteRecord(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting record: " + e.getMessage());
        }
    }
}
