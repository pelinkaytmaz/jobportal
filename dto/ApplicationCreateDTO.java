package com.dauphine.jobportal.dto;

import com.dauphine.jobportal.model.enums.ApplicationStatus;

// DTO for creating a new application
public class ApplicationCreateDTO {
    private Long jobId;
    private JobSeekerCreateDTO jobSeeker;
    private ApplicationStatus status;
    
    // Constructors
    public ApplicationCreateDTO() {
    }
    
    // Getters and Setters
    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public JobSeekerCreateDTO getJobSeeker() {
        return jobSeeker;
    }

    public void setJobSeeker(JobSeekerCreateDTO jobSeeker) {
        this.jobSeeker = jobSeeker;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}