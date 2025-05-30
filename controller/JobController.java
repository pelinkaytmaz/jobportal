package com.dauphine.jobportal.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dauphine.jobportal.dto.JobCreateDTO;
import com.dauphine.jobportal.dto.JobDTO;
import com.dauphine.jobportal.model.Company;
import com.dauphine.jobportal.model.Job;
import com.dauphine.jobportal.model.enums.ExperienceLevel;
import com.dauphine.jobportal.model.enums.JobType;
import com.dauphine.jobportal.service.CompanyService;
import com.dauphine.jobportal.service.JobService;
import com.dauphine.jobportal.util.EntityDTOMapper;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:4200") // Angular runs on port 4200 by default
public class JobController {

    private final JobService jobService;
    private final CompanyService companyService;
    private final EntityDTOMapper mapper;

    @Autowired
    public JobController(JobService jobService, CompanyService companyService, EntityDTOMapper mapper) {
        this.jobService = jobService;
        this.companyService = companyService;
        this.mapper = mapper;
    }

    // Create a new job
    @PostMapping
    public ResponseEntity<JobDTO> createJob(@RequestBody JobCreateDTO jobCreateDTO) {
        // Get the company
        Company company = companyService.getCompanyById(jobCreateDTO.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Company not found with ID: " + jobCreateDTO.getCompanyId()));

        // Convert DTO to entity
        Job job = mapper.toJobEntity(jobCreateDTO, company);

        // Save the job
        Job createdJob = jobService.saveJob(job);

        // Convert entity to DTO
        JobDTO jobDTO = mapper.toJobDTO(createdJob);

        return new ResponseEntity<>(jobDTO, HttpStatus.CREATED);
    }

    // Get all jobs
    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();

        // Convert list of entities to list of DTOs
        List<JobDTO> jobDTOs = jobs.stream()
                .map(mapper::toJobDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(jobDTOs, HttpStatus.OK);
    }

    // Get job by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(job -> {
                    JobDTO jobDTO = mapper.toJobDTO(job);
                    return new ResponseEntity<>(jobDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a job
    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @RequestBody JobCreateDTO jobCreateDTO) {
        return jobService.getJobById(id)
                .map(existingJob -> {
                    // Get the company
                    Company company = companyService.getCompanyById(jobCreateDTO.getCompanyId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Company not found with ID: " + jobCreateDTO.getCompanyId()));

                    // Update existing job with DTO values
                    existingJob.setTitle(jobCreateDTO.getTitle());
                    existingJob.setDescription(jobCreateDTO.getDescription());
                    existingJob.setResponsibilities(jobCreateDTO.getResponsibilities());
                    existingJob.setQualifications(jobCreateDTO.getQualifications());
                    existingJob.setLocation(jobCreateDTO.getLocation());
                    existingJob.setSalaryMin(jobCreateDTO.getSalaryMin());
                    existingJob.setSalaryMax(jobCreateDTO.getSalaryMax());
                    existingJob.setType(jobCreateDTO.getType());
                    existingJob.setExperienceLevel(jobCreateDTO.getExperienceLevel());
                    existingJob.setCompany(company);

                    // Save updated job
                    Job updatedJob = jobService.saveJob(existingJob);

                    // Convert to DTO
                    JobDTO jobDTO = mapper.toJobDTO(updatedJob);

                    return new ResponseEntity<>(jobDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Delete a job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(job -> {
                    jobService.deleteJob(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Search jobs with filters
    @GetMapping("/search")
    public ResponseEntity<List<JobDTO>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType type,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary) {

        List<Job> filteredJobs = jobService.findJobsWithFilters(
                keyword, location, type, experienceLevel, minSalary, maxSalary);

        // Convert to DTOs
        List<JobDTO> jobDTOs = filteredJobs.stream()
                .map(mapper::toJobDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(jobDTOs, HttpStatus.OK);
    }
}