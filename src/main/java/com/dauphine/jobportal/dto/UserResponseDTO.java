package com.dauphine.jobportal.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.dauphine.jobportal.model.Role;
import com.dauphine.jobportal.model.User;
import com.dauphine.jobportal.model.enums.ERole;

public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Set<ERole> roles;
    private Long companyId;
    private String companyName;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        this.companyId = user.hasCompany() ? user.getCompany().getId() : null;
        this.companyName = user.hasCompany() ? user.getCompany().getName() : null;
    }

    public static List<UserResponseDTO> fromUserList(List<User> users) {
        return users.stream().map(UserResponseDTO::new).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Set<ERole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ERole> roles) {
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

}
