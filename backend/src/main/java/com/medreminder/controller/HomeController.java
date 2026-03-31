package com.medreminder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin("*")
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "MedReminder Backend is LIVE! (Running on port 8080)";
    }
}
