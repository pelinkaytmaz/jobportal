package com.dauphine.jobportal.dto;

import java.util.Set;

public class JwtResponseDTO {

    private String token;
    private String type = "Bearer";
    private String username;
    private Set<String> roles;

    public JwtResponseDTO(String token, String username, Set<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    // Getters and setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
