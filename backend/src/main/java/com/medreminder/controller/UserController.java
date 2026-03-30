package com.medreminder.controller;

import com.medreminder.dto.LoginRequest;
import com.medreminder.dto.SignupRequest;
import com.medreminder.model.User;
import com.medreminder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getMobile(), request.getPassword());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam("mobile") String mobile) {
        // Mock OTP send logic
        return ResponseEntity.ok("OTP sent down to " + mobile);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam("otp") String otp) {
        // Mock OTP verification - accepts any OTP 4 chars or more
        if (otp != null && otp.length() >= 4) {
            return ResponseEntity.ok("OTP Verified!");
        }
        return ResponseEntity.badRequest().body("Invalid OTP. Must be at least 4 chars.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.authenticateUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
