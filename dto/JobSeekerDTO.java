package com.dauphine.jobportal.dto;

import java.util.HashSet;
import java.util.Set;

// DTO for returning job seeker data in responses
public class JobSeekerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<ExperienceDTO> experiences = new HashSet<>();
    private Set<SkillDTO> skills = new HashSet<>();

    // Constructors
    public JobSeekerDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Set<ExperienceDTO> getExperiences() {
        return experiences;
    }

    public void setExperiences(Set<ExperienceDTO> experiences) {
        this.experiences = experiences;
    }

    public Set<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(Set<SkillDTO> skills) {
        this.skills = skills;
    }
}
