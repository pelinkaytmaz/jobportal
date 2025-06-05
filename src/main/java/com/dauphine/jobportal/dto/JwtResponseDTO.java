package com.dauphine.jobportal.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

public class JwtResponseDTO {

    @Schema(description = "Identifiant de l'utilisateur", example = "12345")
    private Long id;

    @Schema(description = "Token JWT pour l'authentification", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    @Schema(description = "Type de token", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Nom d'utilisateur", example = "john_doe")
    private String username;

    @Schema(description = "Rôles de l'utilisateur", example = "[\"ROLE_CANDIDAT\", \"ROLE_USER\"]")
    private Set<String> roles;

    @Schema(description = "ID de l'entreprise associée (optionnel)", example = "1")
    private Long companyId;

    @Schema(description = "Nom de l'entreprise associée (optionnel)", example = "Tech Solutions Inc")
    private String companyName;

    // Constructors
    public JwtResponseDTO() {
    }

    public JwtResponseDTO(String token, String username, Set<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    public JwtResponseDTO(String token, String username, Set<String> roles, Long companyId, String companyName) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.companyId = companyId;
        this.companyName = companyName;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    // Utility methods
    public boolean hasCompany() {
        return this.companyId != null && this.companyName != null;
    }

    public boolean hasRoles() {
        return this.roles != null && !this.roles.isEmpty();
    }

    public boolean hasRole(String roleName) {
        return this.roles != null && this.roles.contains(roleName);
    }

    // Fluent builder methods
    public JwtResponseDTO withCompany(Long companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
        return this;
    }

    public JwtResponseDTO withoutCompany() {
        this.companyId = null;
        this.companyName = null;
        return this;
    }
}
