package com.dauphine.jobportal.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

public class SignupRequestDTO {

    @Schema(description = "Nom d'utilisateur unique", example = "john_doe", required = true)
    private String username;

    @Schema(description = "Adresse email unique", example = "john.doe@email.com", required = true)
    private String email;

    @Schema(description = "Mot de passe", example = "password123", required = true)
    private String password;

    @Schema(description = "Liste des rôles à attribuer à l'utilisateur", 
            example = "[{\"name\": \"ROLE_CANDIDAT\"}]")
    private Set<RoleRequest> roles;

    @Schema(description = "ID de l'entreprise à associer à l'utilisateur (optionnel)", 
            example = "1")
    private Long companyId;

    @Schema(description = "Informations du profil candidat (requis si rôle ROLE_CANDIDAT)")
    private JobSeekerCreateDTO jobSeeker;

    public static class RoleRequest {
        @Schema(description = "Nom du rôle", example = "ROLE_CANDIDAT", 
                allowableValues = {"ROLE_CANDIDAT", "ROLE_RECRUTEUR", "ROLE_ADMIN"})
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Constructors
    public SignupRequestDTO() {
    }

    public SignupRequestDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public SignupRequestDTO(String username, String email, String password, Long companyId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.companyId = companyId;
    }

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RoleRequest> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleRequest> roles) {
        this.roles = roles;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public JobSeekerCreateDTO getJobSeeker() {
        return jobSeeker;
    }

    public void setJobSeeker(JobSeekerCreateDTO jobSeeker) {
        this.jobSeeker = jobSeeker;
    }

    // Utility methods
    public boolean hasCompany() {
        return this.companyId != null;
    }

    public boolean hasRoles() {
        return this.roles != null && !this.roles.isEmpty();
    }

    public boolean hasJobSeekerData() {
        return this.jobSeeker != null;
    }
}