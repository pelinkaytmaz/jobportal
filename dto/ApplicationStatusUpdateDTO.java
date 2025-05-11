package com.dauphine.jobportal.dto;

import com.dauphine.jobportal.model.enums.ApplicationStatus;

// DTO for updating application status
public class ApplicationStatusUpdateDTO {
    private ApplicationStatus status;
    
    // Constructors
    public ApplicationStatusUpdateDTO() {
    }
    
    public ApplicationStatusUpdateDTO(ApplicationStatus status) {
        this.status = status;
    }
    
    // Getters and Setters
    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
