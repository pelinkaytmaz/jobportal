package com.dauphine.jobportal.dto;

import com.dauphine.jobportal.model.User;

import java.util.HashSet;
import java.util.Set;

// DTO for creating a new job seeker
public class JobSeekerCreateDTO {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<ExperienceCreateDTO> experiences = new HashSet<>();
    private Set<String> skills = new HashSet<>();
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Constructors
    public JobSeekerCreateDTO() {
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<ExperienceCreateDTO> getExperiences() {
        return experiences;
    }

    public void setExperiences(Set<ExperienceCreateDTO> experiences) {
        this.experiences = experiences;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }
}