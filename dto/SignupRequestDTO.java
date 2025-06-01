package com.dauphine.jobportal.dto;

import java.util.Set;

public class SignupRequestDTO {

    private String username;
    private String email;
    private String password;
    private Set<RoleRequest> roles;
    private JobSeekerCreateDTO jobSeeker;

    public static class RoleRequest {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
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

    public JobSeekerCreateDTO getJobSeeker() {
        return jobSeeker;
    }

    public void setJobSeeker(JobSeekerCreateDTO jobSeeker) {
        this.jobSeeker = jobSeeker;
    }
}
