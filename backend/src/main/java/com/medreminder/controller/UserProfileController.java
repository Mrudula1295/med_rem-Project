package com.medreminder.controller;

import com.medreminder.model.User;
import com.medreminder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@NonNull @PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-profile")
    @SuppressWarnings("null")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Integer age = payload.containsKey("age") && payload.get("age") != null ? 
                         Integer.valueOf(payload.get("age").toString()) : null;
            String gender = payload.containsKey("gender") ? (String) payload.get("gender") : null;
            Double weight = payload.containsKey("weight") && payload.get("weight") != null ? 
                           Double.valueOf(payload.get("weight").toString()) : null;
            String healthConditions = payload.containsKey("healthConditions") ? (String) payload.get("healthConditions") : null;

            User updatedUser = userService.updateProfile(userId, age, gender, weight, healthConditions);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update profile: " + e.getMessage());
        }
    }
}
