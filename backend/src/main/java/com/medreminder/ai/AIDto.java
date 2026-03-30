package com.medreminder.ai;

import lombok.Data;
import java.util.List;

public class AIDto {

    @Data
    public static class AICheckRequest {
        private Long userId;
        private String medicineName;
        private List<String> conditions;
        private String imageText;
    }
}
