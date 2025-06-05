package com.dauphine.jobportal.dto;

import java.time.LocalDate;

// DTO for creating a new experience
public class ExperienceCreateDTO {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Constructors
    public ExperienceCreateDTO() {
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}