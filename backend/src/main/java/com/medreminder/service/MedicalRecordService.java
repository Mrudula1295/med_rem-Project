package com.medreminder.service;

import com.medreminder.model.MedicalRecord;
import com.medreminder.model.User;
import com.medreminder.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.util.List;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private UserService userService;

    private final String UPLOAD_DIR = "uploads/medical-records/";

    public MedicalRecord uploadRecord(@NonNull Long userId, String title, MultipartFile file) throws Exception {
        User user = userService.getUserById(userId);
        
        MedicalRecord record = new MedicalRecord();
        record.setTitle(title);
        record.setUser(user);

        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            record.setFilePath("/uploads/medical-records/" + filename);
        }

        return medicalRecordRepository.save(record);
    }

    public List<MedicalRecord> getUserRecords(Long userId) {
        return medicalRecordRepository.findByUserId(userId);
    }

    public void deleteRecord(@NonNull Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Record not found"));

        if (record.getFilePath() != null) {
            try {
                // Remove the initial / from the file path to formulate correct relative path from project root
                String relativePath = record.getFilePath().startsWith("/") ? record.getFilePath().substring(1) : record.getFilePath();
                Path filePath = Paths.get(relativePath);
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                System.err.println("Failed to delete record file: " + e.getMessage());
            }
        }
        
        medicalRecordRepository.deleteById(id);
    }
}
