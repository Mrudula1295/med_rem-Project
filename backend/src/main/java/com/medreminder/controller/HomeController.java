package com.medreminder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin("*")
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "<h1>🚀 MedReminder Backend is LIVE!</h1>" +
               "<p>The API is running and connected to the database.</p>" +
               "<p>Use your <b>Vercel</b> link to access the dashboard.</p>" +
               "<p><b>API Base:</b> /api</p>";
    }
}
