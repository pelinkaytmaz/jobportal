package com.dauphine.jobportal.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dauphine.jobportal.dto.JwtResponseDTO;
import com.dauphine.jobportal.dto.LoginRequestDTO;
import com.dauphine.jobportal.dto.SignupRequestDTO;
import com.dauphine.jobportal.model.Role;
import com.dauphine.jobportal.model.User;
import com.dauphine.jobportal.model.enums.ERole;
import com.dauphine.jobportal.repository.RoleRepository;
import com.dauphine.jobportal.repository.UserRepository;
import com.dauphine.jobportal.security.JwtUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentification", description = "API d'authentification et de gestion des utilisateurs")
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
        @Operation(
                summary = "Inscription d'un nouvel utilisateur",
                description = "Permet de créer un nouveau compte utilisateur avec un nom d'utilisateur, email et mot de passe uniques. " +
                            "Si aucun rôle n'est spécifié, l'utilisateur recevra le rôle ROLE_USER par défaut."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Utilisateur créé avec succès",
                        content = @Content(
                                mediaType = "text/plain",
                                examples = @ExampleObject(value = "User registered successfully!")
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Erreur lors de la création - nom d'utilisateur ou email déjà utilisé",
                        content = @Content(
                                mediaType = "text/plain",
                                examples = {
                                        @ExampleObject(
                                                name = "Username taken",
                                                value = "Error: Username is already taken!"
                                        ),
                                        @ExampleObject(
                                                name = "Email in use",
                                                value = "Error: Email is already in use!"
                                        )
                                }
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erreur interne - rôle non trouvé",
                        content = @Content(
                                mediaType = "text/plain",
                                examples = @ExampleObject(value = "Error: Role ROLE_USER is not found.")
                        )
                )
        })
        public String registerUser(
                @Parameter(
                        description = "Informations d'inscription de l'utilisateur",
                        required = true,
                        schema = @Schema(implementation = SignupRequestDTO.class)
                )
                @Valid @RequestBody SignupRequestDTO signUpRequest) {
                
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
        @Operation(
                summary = "Connexion utilisateur",
                description = "Authentifie un utilisateur avec ses identifiants et retourne un token JWT pour les requêtes ultérieures. " +
                            "Le token doit être inclus dans l'en-tête Authorization des requêtes suivantes."
        )
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Authentification réussie",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = JwtResponseDTO.class),
                                examples = @ExampleObject(
                                        value = """
                                        {
                                          "token": "eyJhbGciOiJIUzUxMiJ9...",
                                          "username": "john_doe",
                                          "roles": ["ROLE_USER"]
                                        }
                                        """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Identifiants invalides",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = """
                                        {
                                          "error": "Unauthorized",
                                          "message": "Invalid credentials"
                                        }
                                        """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Utilisateur non trouvé",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = """
                                        {
                                          "error": "User not found",
                                          "message": "Username not found"
                                        }
                                        """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erreur interne du serveur",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = """
                                        {
                                          "error": "Internal Server Error",
                                          "message": "Authentication service unavailable"
                                        }
                                        """
                                )
                        )
                )
        })
        public JwtResponseDTO authenticateUser(
                @Parameter(
                        description = "Identifiants de connexion (nom d'utilisateur et mot de passe)",
                        required = true,
                        schema = @Schema(implementation = LoginRequestDTO.class)
                )
                @Valid @RequestBody LoginRequestDTO loginRequest) {
                
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