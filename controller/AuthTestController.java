package com.dauphine.jobportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.dauphine.jobportal.dto.MessageResponse;
import com.dauphine.jobportal.model.enums.ERole;
import com.dauphine.jobportal.model.Role;
import com.dauphine.jobportal.model.User;
import com.dauphine.jobportal.repository.RoleRepository;
import com.dauphine.jobportal.repository.UserRepository;
import com.dauphine.jobportal.security.jwt.JwtUtils;
import com.dauphine.jobportal.security.services.UserDetailsImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth-test")
public class AuthTestController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Auth test endpoint is working");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdminUser() {
        // Vérifier si l'administrateur existe déjà
        if (userRepository.existsByUsername("admin")) {
            return ResponseEntity.ok(new MessageResponse("Admin user already exists. Username: admin, Password: admin123"));
        }

        // Créer l'utilisateur admin
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@jobportal.com");
        adminUser.setPassword(encoder.encode("admin123"));

        Set<Role> roles = new HashSet<>();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
        roles.add(adminRole);
        adminUser.setRoles(roles);

        userRepository.save(adminUser);

        return ResponseEntity.ok(new MessageResponse("Admin user created successfully. Username: admin, Password: admin123"));
    }

    @PostMapping("/generate-token")
    public ResponseEntity<?> generateToken(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", userDetails.getUsername());
            response.put("email", userDetails.getEmail());
            response.put("message", "Token generated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Authentication failed");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.substring(7); // Remove "Bearer " prefix
            boolean isValid = jwtUtils.validateJwtToken(jwt);
            
            Map<String, Object> response = new HashMap<>();
            if (isValid) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                response.put("valid", true);
                response.put("username", username);
                response.put("message", "Token is valid");
            } else {
                response.put("valid", false);
                response.put("message", "Token is invalid");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", "Token validation failed");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}