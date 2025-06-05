package com.dauphine.jobportal.dto;

import java.time.LocalDate;

import com.dauphine.jobportal.model.enums.ApplicationStatus;

// DTO for returning application data in responses
public class ApplicationDTO {
    private Long id;
    private JobDTO job;
    private JobSeekerDTO jobSeeker;
    private LocalDate applicationDate;
    private ApplicationStatus status;
    
    // Constructors
    public ApplicationDTO() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobDTO getJob() {
        return job;
    }

    public void setJob(JobDTO job) {
        this.job = job;
    }

    public JobSeekerDTO getJobSeeker() {
        return jobSeeker;
    }

    public void setJobSeeker(JobSeekerDTO jobSeeker) {
        this.jobSeeker = jobSeeker;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}



