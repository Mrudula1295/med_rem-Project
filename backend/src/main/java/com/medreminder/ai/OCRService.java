package com.medreminder.ai;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Random;

@Service
public class OCRService {

    // Simple mocked OCR service to avoid native OS dependencies (Tesseract)
    public String extractTextFromImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return "";
        }
        
        // Mocked logic for demonstration
        String originalFilename = imageFile.getOriginalFilename();
        String fileName = originalFilename != null ? originalFilename.toLowerCase() : "";
        
        if (fileName.contains("para") || fileName.contains("dolo")) {
            return "PARACETAMOL 500mg\nIndications: Fever, Pain relief.";
        } else if (fileName.contains("metformin")) {
            return "METFORMIN 500mg\nIndications: Type 2 Diabetes.";
        } else if (fileName.contains("amlo")) {
            return "AMLODIPINE 5mg\nIndications: Blood Pressure.";
        }
        
        // Generic fallback
        String[] randomMedications = {"Ibuprofen 400mg", "Cetirizine 10mg", "Azithromycin 500mg", "Vitamin C 1000mg"};
        int randomIndex = new Random().nextInt(randomMedications.length);
        return randomMedications[randomIndex] + "\nComposition details found in image.";
    }
}
