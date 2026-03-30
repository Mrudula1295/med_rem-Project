package com.medreminder.ai;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AIService {

    public Map<String, Object> checkMedicineSafety(String medicineName, String imageText, List<String> userConditions) {
        Map<String, Object> result = new HashMap<>();
        
        String medNameLower = medicineName != null ? medicineName.toLowerCase() : "";
        String textLower = imageText != null ? imageText.toLowerCase() : "";
        String fullContext = medNameLower + " " + textLower;
        
        boolean isSafe = true;
        String warning = "None";
        String dosage = "1 tablet";
        String timing = "After food";
        
        // Rule 1: Diabetes & sugar dependencies
        if (userConditions != null && userConditions.contains("diabetes")) {
            if (fullContext.contains("sugar") || fullContext.contains("syrup") || fullContext.contains("sweet")) {
                isSafe = false;
                warning = "Warning: Patient has Diabetes. Medicine may contain sugar/syrup which can spike blood glucose.";
            }
        }
        
        // Rule 2: Blood pressure
        if (userConditions != null && userConditions.contains("blood pressure")) {
            if (fullContext.contains("sodium") || fullContext.contains("pseudoephedrine") || fullContext.contains("nsaid")) {
                isSafe = false;
                warning = "Warning: Patient has BP issues. This medicine may increase blood pressure.";
            }
        }
        
        // Rule 3: Thyroid
        if (userConditions != null && userConditions.contains("thyroid")) {
            if (fullContext.contains("calcium") || fullContext.contains("iron")) {
                warning = "Caution: Take at least 4 hours apart from thyroid medication to avoid absorption issues.";
                timing = "On empty stomach, space 4 hours from other meds";
            }
        }
        
        // Fallback static rules for common meds
        if (medNameLower.contains("paracetamol") || medNameLower.contains("dolo")) {
            dosage = "500mg-650mg every 6-8 hours as needed";
            timing = "After food to avoid upset stomach";
        } else if (medNameLower.contains("ibuprofen")) {
            dosage = "400mg every 6 hours max";
            timing = "Strictly after food";
        } else if (medNameLower.contains("pantoprazole") || medNameLower.contains("omeprazole")) {
            dosage = "1 tablet daily";
            timing = "30 mins before breakfast";
        }
        
        result.put("isSafe", isSafe);
        result.put("dosage", dosage);
        result.put("timing", timing);
        result.put("warnings", warning);
        
        return result;
    }
}
