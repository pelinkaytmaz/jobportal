package com.dauphine.jobportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/public")
public class PublicTestController {
    
    @GetMapping("/test")
    public ResponseEntity<?> publicTest() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint that anyone can access");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}