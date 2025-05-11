package com.dauphine.jobportal.service;

import com.dauphine.jobportal.model.Application;
import com.dauphine.jobportal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    // Save an application
    @Transactional
    public Application saveApplication(Application application) {
        // Set application date if not already set
        if (application.getApplicationDate() == null) {
            application.setApplicationDate(LocalDate.now());
        }
        
        return applicationRepository.save(application);
    }

    // Get all applications
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // Get application by ID
    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    // Get applications by job seeker ID
    public List<Application> getApplicationsByJobSeekerId(Long jobSeekerId) {
        return applicationRepository.findByJobSeekerId(jobSeekerId);
    }

    // Get applications by job ID
    public List<Application> getApplicationsByJobId(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    // Delete an application
    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
}