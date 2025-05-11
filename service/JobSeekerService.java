package com.dauphine.jobportal.service;

import com.dauphine.jobportal.model.JobSeeker;
import com.dauphine.jobportal.repository.JobSeekerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;

    @Autowired
    public JobSeekerService(JobSeekerRepository jobSeekerRepository) {
        this.jobSeekerRepository = jobSeekerRepository;
    }

    // Save a job seeker
    @Transactional
    public JobSeeker saveJobSeeker(JobSeeker jobSeeker) {
        return jobSeekerRepository.save(jobSeeker);
    }

    // Get all job seekers
    public List<JobSeeker> getAllJobSeekers() {
        return jobSeekerRepository.findAll();
    }

    // Get job seeker by ID
    public Optional<JobSeeker> getJobSeekerById(Long id) {
        return jobSeekerRepository.findById(id);
    }

    // Delete a job seeker
    public void deleteJobSeeker(Long id) {
        jobSeekerRepository.deleteById(id);
    }

    // Find job seeker by email
    public Optional<JobSeeker> findByEmail(String email) {
        return jobSeekerRepository.findByEmail(email);
    }
}