package com.dauphine.jobportal.service;

import com.dauphine.jobportal.model.Job;
import com.dauphine.jobportal.model.enums.ExperienceLevel;
import com.dauphine.jobportal.model.enums.JobType;
import com.dauphine.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Save a job
    public Job saveJob(Job job) {
        // Set posting date if not already set
        if (job.getPostingDate() == null) {
            job.setPostingDate(LocalDate.now());
        }
        return jobRepository.save(job);
    }

    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Get job by ID
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    // Delete a job
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    // Find jobs by title containing keyword
    public List<Job> findJobsByTitleContaining(String keyword) {
        return jobRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Find jobs by location
    public List<Job> findJobsByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    // Find jobs by type
    public List<Job> findJobsByType(JobType type) {
        return jobRepository.findByType(type);
    }

    // Find jobs by experience level
    public List<Job> findJobsByExperienceLevel(ExperienceLevel experienceLevel) {
        return jobRepository.findByExperienceLevel(experienceLevel);
    }

    // Find jobs by salary range
    public List<Job> findJobsBySalaryRange(Double minSalary, Double maxSalary) {
        return jobRepository.findBySalaryMinGreaterThanEqualAndSalaryMaxLessThanEqual(minSalary, maxSalary);
    }

    // Search jobs with filters
    public List<Job> findJobsWithFilters(
            String keyword,
            String location,
            JobType type,
            ExperienceLevel experienceLevel,
            Double minSalary,
            Double maxSalary) {

        return jobRepository.findJobsWithFilters(
                keyword,
                location,
                type != null ? type.name() : null,
                experienceLevel != null ? experienceLevel.name() : null,
                minSalary,
                maxSalary);
    }

}