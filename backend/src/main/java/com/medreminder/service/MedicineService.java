package com.medreminder.service;

import com.medreminder.model.Medicine;
import com.medreminder.model.Reminder;
import com.medreminder.model.User;
import com.medreminder.repository.MedicineRepository;
import com.medreminder.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.util.List;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private UserService userService;

    private final String UPLOAD_DIR = "uploads/medicine-images/";

    public Medicine addMedicine(@NonNull Long userId, String name, MultipartFile image, 
                                Boolean aiSafe, String aiDosage, String aiTiming, String aiWarning) throws Exception {
        User user = userService.getUserById(userId);
        
        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setUser(user);
        
        if (aiSafe != null) medicine.setAiSafe(aiSafe);
        if (aiDosage != null) medicine.setAiDosage(aiDosage);
        if (aiTiming != null) medicine.setAiTiming(aiTiming);
        if (aiWarning != null) medicine.setAiWarning(aiWarning);

        if (image != null && !image.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            medicine.setImagePath("/uploads/medicine-images/" + filename);
        }

        return medicineRepository.save(medicine);
    }

    public List<Medicine> getUserMedicines(Long userId) {
        return medicineRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteMedicine(@NonNull Long id) {
        List<Reminder> reminders = reminderRepository.findByMedicineId(id);
        if (reminders != null && !reminders.isEmpty()) {
            reminderRepository.deleteAll(reminders);
        }

        Medicine medicine = medicineRepository.findById(id).orElse(null);
        if (medicine != null && medicine.getImagePath() != null) {
            try {
                String relativePath = medicine.getImagePath().startsWith("/") ? medicine.getImagePath().substring(1) : medicine.getImagePath();
                Path filePath = Paths.get(relativePath);
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                System.err.println("Failed to delete medicine image: " + e.getMessage());
            }
        }

        medicineRepository.deleteById(id);
    }
}
