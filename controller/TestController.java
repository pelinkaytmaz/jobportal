package com.dauphine.jobportal.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dauphine.jobportal.dto.MessageResponse;
import com.dauphine.jobportal.model.enums.ERole;
import com.dauphine.jobportal.model.Role;
import com.dauphine.jobportal.model.User;
import com.dauphine.jobportal.repository.RoleRepository;
import com.dauphine.jobportal.repository.UserRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @GetMapping("/public")
    public ResponseEntity<?> publicAccess() {
        return ResponseEntity.ok(new MessageResponse("Public content."));
    }
    
    @GetMapping("/user")
    public ResponseEntity<?> userAccess() {
        return ResponseEntity.ok(new MessageResponse("User content."));
    }
    
    @GetMapping("/jobseeker")
    public ResponseEntity<?> jobSeekerAccess() {
        return ResponseEntity.ok(new MessageResponse("JobSeeker content."));
    }
    
    @GetMapping("/employer")
    public ResponseEntity<?> employerAccess() {
        return ResponseEntity.ok(new MessageResponse("Employer content."));
    }
    
    @GetMapping("/admin")
    public ResponseEntity<?> adminAccess() {
        return ResponseEntity.ok(new MessageResponse("Admin content."));
    }
    
    // Endpoint pour créer un utilisateur test (seulement pour les tests, à retirer en production)
    @PostMapping("/setup")
    public ResponseEntity<?> setupTestUsers() {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername("testuser")) {
            return ResponseEntity.ok(new MessageResponse("Test users already exist. No action taken."));
        }
        
        // Créer un utilisateur admin
        User adminUser = new User("admin", "admin@jobportal.com", encoder.encode("admin123"));
        Set<Role> adminRoles = new HashSet<>();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);
        userRepository.save(adminUser);
        
        // Créer un utilisateur normal
        User normalUser = new User("testuser", "user@jobportal.com", encoder.encode("user123"));
        Set<Role> userRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        userRoles.add(userRole);
        normalUser.setRoles(userRoles);
        userRepository.save(normalUser);
        
        // Créer un chercheur d'emploi
        User jobSeeker = new User("jobseeker", "jobseeker@jobportal.com", encoder.encode("seeker123"));
        Set<Role> seekerRoles = new HashSet<>();
        Role seekerRole = roleRepository.findByName(ERole.ROLE_JOBSEEKER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        seekerRoles.add(seekerRole);
        jobSeeker.setRoles(seekerRoles);
        userRepository.save(jobSeeker);
        
        // Créer un employeur
        User employer = new User("employer", "employer@jobportal.com", encoder.encode("employer123"));
        Set<Role> employerRoles = new HashSet<>();
        Role employerRole = roleRepository.findByName(ERole.ROLE_EMPLOYER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        employerRoles.add(employerRole);
        employer.setRoles(employerRoles);
        userRepository.save(employer);
        
        return ResponseEntity.ok(new MessageResponse("Test users have been created successfully!"));
    }
}