package com.medreminder.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin("*")
public class AIController {

    @Autowired
    private OCRService ocrService;
    
    @Autowired
    private AIService aiService;

    @PostMapping("/analyze-medicine")
    public ResponseEntity<?> analyzeMedicineImage(@RequestParam("image") MultipartFile image) {
        String extractedText = ocrService.extractTextFromImage(image);
        Map<String, String> response = new HashMap<>();
        response.put("text", extractedText);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-medicine")
    public ResponseEntity<?> checkMedicine(@RequestBody AIDto.AICheckRequest request) {
        Map<String, Object> aiResult = aiService.checkMedicineSafety(
            request.getMedicineName(),
            request.getImageText(),
            request.getConditions()
        );
        return ResponseEntity.ok(aiResult);
    }
}
