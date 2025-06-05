package com.dauphine.jobportal.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dauphine.jobportal.dto.UserResponseDTO;
import com.dauphine.jobportal.dto.UserUpdateDTO;
import com.dauphine.jobportal.model.Company;
import com.dauphine.jobportal.model.User;
import com.dauphine.jobportal.repository.CompanyRepository;
import com.dauphine.jobportal.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "API CRUD de gestion des utilisateurs")
@SecurityRequirement(name = "JWT")
public class UserController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    // Gestion globale des erreurs
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (message.contains("introuvable") || message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message.contains("déjà utilisé") || message.contains("already exists")) {
            status = HttpStatus.CONFLICT;
        }

        return ResponseEntity.status(status)
                .body(Map.of(
                        "error", status.getReasonPhrase(),
                        "message", message,
                        "status", String.valueOf(status.value())));
    }

    

    // READ - Get all users (Admin only)
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Récupérer tous les utilisateurs", description = "Retourne la liste de tous les utilisateurs (accès admin requis)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "403", description = "Accès interdit - droits admin requis")
    })
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userDTOs = UserResponseDTO.fromUserList(users);
        return ResponseEntity.ok(userDTOs);
    }

    // READ - Get user by ID (Admin only)
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Récupérer un utilisateur par ID", description = "Retourne les détails d'un utilisateur spécifique (accès admin requis)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        Optional<User> user = userRepository.findByIdWithCompanyAndRoles(id);
        return user
                .map(u -> ResponseEntity.ok(new UserResponseDTO(u)))
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + id));
    }

    
    // UPDATE - Update user by ID (Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Mettre à jour un utilisateur par ID", description = "Permet à un admin de modifier les informations d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
            @Parameter(description = "Nouvelles informations utilisateur") @Valid @RequestBody UserUpdateDTO updateDTO) {

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur introuvable avec l'ID : " + id);
        }

        User user = userOpt.get();

        if (updateDTO.hasEmail()) {
            if (!updateDTO.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("Email déjà utilisé");
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.hasUsername()) {
            if (!updateDTO.getUsername().equals(user.getUsername()) &&
                    userRepository.existsByUsername(updateDTO.getUsername())) {
                throw new RuntimeException("Nom d'utilisateur déjà utilisé");
            }
            user.setUsername(updateDTO.getUsername());
        }

        if (updateDTO.hasPassword()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        if (updateDTO.hasCompanyId()) {
            if (updateDTO.getCompanyId() == 0) {
                user.setCompany(null);
            } else {
                Company company = companyRepository.findById(updateDTO.getCompanyId())
                        .orElseThrow(() -> new RuntimeException(
                                "Entreprise introuvable avec l'ID : " + updateDTO.getCompanyId()));
                user.setCompany(company);
            }
        }

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(new UserResponseDTO(updatedUser));
    }

    

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Rechercher des utilisateurs", description = "Recherche des utilisateurs par nom d'utilisateur ou email (admin uniquement)")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(
            @Parameter(description = "Terme de recherche") @RequestParam String query) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
        List<UserResponseDTO> dtos = users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_RECRUTEUR')")
    @Operation(summary = "Récupérer les utilisateurs d'une entreprise", description = "Retourne tous les utilisateurs associés à une entreprise")
    public ResponseEntity<List<UserResponseDTO>> getUsersByCompany(
            @Parameter(description = "ID de l'entreprise") @PathVariable Long companyId) {
        List<User> users = userRepository.findByCompany_Id(companyId);
        List<UserResponseDTO> dtos = users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

}