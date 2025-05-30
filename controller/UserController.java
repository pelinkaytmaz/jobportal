package com.dauphine.jobportal.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dauphine.jobportal.model.User;
import com.dauphine.jobportal.repository.UserRepository;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "API de gestion des profils utilisateurs authentifiés")
@SecurityRequirement(name = "JWT")
public class UserController {

    private final UserRepository userRepository;

    // @GetMapping("/me")
    // @Operation(
    //     summary = "Récupérer le profil de l'utilisateur connecté",
    //     description = "Retourne les informations du profil de l'utilisateur actuellement authentifié " +
    //                  "basé sur le token JWT fourni. Cet endpoint permet à l'utilisateur de consulter " +
    //                  "ses propres données de profil."
    // )
    // @ApiResponses(value = {
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "Profil utilisateur récupéré avec succès",
    //         content = @Content(
    //             mediaType = "application/json",
    //             schema = @Schema(implementation = User.class),
    //             examples = @ExampleObject(
    //                 value = """
    //                 {
    //                   "id": 1,
    //                   "username": "john_doe",
    //                   "email": "john.doe@email.com",
    //                   "roles": [
    //                     {
    //                       "id": 1,
    //                       "name": "ROLE_USER"
    //                     }
    //                   ],
    //                   "createdAt": "2024-01-15T10:30:00Z",
    //                   "updatedAt": "2024-01-20T14:45:00Z",
    //                   "enabled": true,
    //                   "accountNonExpired": true,
    //                   "accountNonLocked": true,
    //                   "credentialsNonExpired": true
    //                 }
    //                 """
    //             )
    //         )
    //     ),
    //     @ApiResponse(
    //         responseCode = "401",
    //         description = "Non autorisé - Token JWT manquant, invalide ou expiré",
    //         content = @Content(
    //             mediaType = "application/json",
    //             examples = @ExampleObject(
    //                 value = """
    //                 {
    //                   "error": "Unauthorized",
    //                   "message": "Token JWT requis pour accéder à cette ressource",
    //                   "timestamp": "2024-01-20T15:30:00Z"
    //                 }
    //                 """
    //             )
    //         )
    //     ),
    //     @ApiResponse(
    //         responseCode = "403",
    //         description = "Accès interdit - Token valide mais utilisateur sans permissions",
    //         content = @Content(
    //             mediaType = "application/json",
    //             examples = @ExampleObject(
    //                 value = """
    //                 {
    //                   "error": "Forbidden",
    //                   "message": "Accès refusé à cette ressource",
    //                   "timestamp": "2024-01-20T15:30:00Z"
    //                 }
    //                 """
    //             )
    //         )
    //     ),
    //     @ApiResponse(
    //         responseCode = "404",
    //         description = "Utilisateur introuvable - Le compte a pu être supprimé",
    //         content = @Content(
    //             mediaType = "application/json",
    //             examples = @ExampleObject(
    //                 value = """
    //                 {
    //                   "error": "Not Found",
    //                   "message": "Profil utilisateur introuvable",
    //                   "timestamp": "2024-01-20T15:30:00Z"
    //                 }
    //                 """
    //             )
    //         )
    //     ),
    //     @ApiResponse(
    //         responseCode = "500",
    //         description = "Erreur interne du serveur",
    //         content = @Content(
    //             mediaType = "application/json",
    //             examples = @ExampleObject(
    //                 value = """
    //                 {
    //                   "error": "Internal Server Error",
    //                   "message": "Une erreur inattendue s'est produite",
    //                   "timestamp": "2024-01-20T15:30:00Z"
    //                 }
    //                 """
    //             )
    //         )
    //     )
    // })
    public User getCurrentUser(
        @Parameter(
            description = "Utilisateur authentifié automatiquement injecté via le token JWT",
            hidden = true
        )
        @AuthenticationPrincipal User user) {
        return user;
    }
}