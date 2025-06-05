package com.dauphine.jobportal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {

    @Schema(description = "Nouveau nom d'utilisateur", example = "john_doe_updated")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    @Schema(description = "Nouvelle adresse email", example = "john.updated@email.com")
    @Email(message = "Format d'email invalide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;

    @Schema(description = "Nouveau mot de passe", example = "newPassword123")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;

    @Schema(description = "ID de la nouvelle entreprise à associer (optionnel)", example = "2")
    private Long companyId;

    // Constructors
    public UserUpdateDTO() {
    }

    public UserUpdateDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public UserUpdateDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public UserUpdateDTO(String username, String email, String password, Long companyId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.companyId = companyId;
    }

    // Getters and setters
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    // Utility methods
    public boolean hasUsername() {
        return this.username != null && !this.username.trim().isEmpty();
    }

    public boolean hasEmail() {
        return this.email != null && !this.email.trim().isEmpty();
    }

    public boolean hasPassword() {
        return this.password != null && !this.password.trim().isEmpty();
    }

    public boolean hasCompanyId() {
        return this.companyId != null;
    }

    @Schema(hidden = true)
    public boolean isEmpty() {
        return !hasUsername() && !hasEmail() && !hasPassword() && !hasCompanyId();
    }

    // Builder pattern methods
    public static UserUpdateDTOBuilder builder() {
        return new UserUpdateDTOBuilder();
    }

    public static class UserUpdateDTOBuilder {
        private String username;
        private String email;
        private String password;
        private Long companyId;

        public UserUpdateDTOBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserUpdateDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserUpdateDTOBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserUpdateDTOBuilder companyId(Long companyId) {
            this.companyId = companyId;
            return this;
        }

        public UserUpdateDTO build() {
            return new UserUpdateDTO(username, email, password, companyId);
        }
    }

    // toString pour le debugging
    @Override
    public String toString() {
        return "UserUpdateDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + (password != null ? "[PROTECTED]" : "null") + '\'' +
                ", companyId=" + companyId +
                '}';
    }

    // equals et hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UserUpdateDTO that = (UserUpdateDTO) o;

        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;
        if (email != null ? !email.equals(that.email) : that.email != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null)
            return false;
        return companyId != null ? companyId.equals(that.companyId) : that.companyId == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (companyId != null ? companyId.hashCode() : 0);
        return result;
    }
}