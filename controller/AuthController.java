package com.dauphine.jobportal.controller;

import com.dauphine.jobportal.dto.JwtResponseDTO;
import com.dauphine.jobportal.dto.LoginRequestDTO;
import com.dauphine.jobportal.dto.SignupRequestDTO;
import com.dauphine.jobportal.model.Role;
import com.dauphine.jobportal.model.User;
import com.dauphine.jobportal.model.enums.ERole;
import com.dauphine.jobportal.repository.RoleRepository;
import com.dauphine.jobportal.repository.UserRepository;
import com.dauphine.jobportal.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

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

        @PostMapping("/signup")
        public String registerUser(@Valid @RequestBody SignupRequestDTO signUpRequest) {
                if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                        return "Error: Username is already taken!";
                }

                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                        return "Error: Email is already in use!";
                }

                User user = new User(
                                signUpRequest.getUsername(),
                                signUpRequest.getEmail(),
                                encoder.encode(signUpRequest.getPassword()));

                Set<Role> roles = new HashSet<>();

                if (signUpRequest.getRoles() == null || signUpRequest.getRoles().isEmpty()) {
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                        .orElseThrow(() -> new RuntimeException("Error: Role ROLE_USER is not found."));
                        roles.add(userRole);
                } else {
                        signUpRequest.getRoles().forEach(roleReq -> {
                                ERole roleEnum = ERole.valueOf(roleReq.getName());
                                Role role = roleRepository.findByName(roleEnum)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Error: Role " + roleReq.getName() + " is not found."));
                                roles.add(role);
                        });
                }

                user.setRoles(roles);
                userRepository.save(user);

                return "User registered successfully!";
        }

        @PostMapping("/login")
        public JwtResponseDTO authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                        loginRequest.getPassword()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        String jwt = jwtUtils.generateJwtToken(authentication);

                        var userDetails = (org.springframework.security.core.userdetails.User) authentication
                                        .getPrincipal();

                        Set<String> roles = userDetails.getAuthorities().stream()
                                        .map(item -> item.getAuthority())
                                        .collect(Collectors.toSet());

                        return new JwtResponseDTO(jwt,
                                        userDetails.getUsername(),
                                        roles);

                } catch (BadCredentialsException e) {
                        System.out.println("Authentication failed: bad credentials");
                        throw e;
                } catch (UsernameNotFoundException e) {
                        System.out.println("Authentication failed: user not found");
                        throw e;
                } catch (Exception e) {
                        System.out.println("Authentication failed: " + e.getMessage());
                        throw e;
                }
        }

}
