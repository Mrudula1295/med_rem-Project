package com.medreminder.service;

import com.medreminder.model.User;
import com.medreminder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(String username, String email, String mobile, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.findByMobile(mobile).isPresent()) {
            throw new RuntimeException("Mobile number already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setMobile(mobile);
        // Storing as plain text for simplicity per typical beginner projects. 
        // In production, use BCryptPasswordEncoder.
        user.setPassword(password);
        return userRepository.save(user);
    }

    public User authenticateUser(String identifier, String password) {
        // Try to find user by username, email, or mobile
        Optional<User> user = userRepository.findByUsername(identifier);
        
        if (user.isEmpty()) {
            user = userRepository.findByEmail(identifier);
        }
        if (user.isEmpty()) {
            user = userRepository.findByMobile(identifier);
        }

        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }
        throw new RuntimeException("Invalid credentials");
    }

    public User getUserById(@NonNull Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @SuppressWarnings("null")
    public User updateProfile(@NonNull Long id, Integer age, String gender, Double weight, String healthConditions) {
        User user = getUserById(id);
        if (age != null) user.setAge(age);
        if (gender != null) user.setGender(gender);
        if (weight != null) user.setWeight(weight);
        if (healthConditions != null) user.setHealthConditions(healthConditions);
        
        return userRepository.save(user);
    }
}
