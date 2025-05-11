package com.dauphine.jobportal.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dauphine.jobportal.dto.ApplicationCreateDTO;
import com.dauphine.jobportal.dto.ApplicationDTO;
import com.dauphine.jobportal.dto.ApplicationStatusUpdateDTO;
import com.dauphine.jobportal.model.Application;
import com.dauphine.jobportal.model.Job;
import com.dauphine.jobportal.model.JobSeeker;
import com.dauphine.jobportal.model.Skill;
import com.dauphine.jobportal.service.ApplicationService;
import com.dauphine.jobportal.service.JobSeekerService;
import com.dauphine.jobportal.service.JobService;
import com.dauphine.jobportal.service.SkillService;
import com.dauphine.jobportal.util.EntityDTOMapper;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:4200")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobService jobService;
    private final JobSeekerService jobSeekerService;
    private final SkillService skillService;
    private final EntityDTOMapper mapper;

    @Autowired
    public ApplicationController(ApplicationService applicationService,
                                JobService jobService,
                                JobSeekerService jobSeekerService,
                                SkillService skillService,
                                EntityDTOMapper mapper) {
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.jobSeekerService = jobSeekerService;
        this.skillService = skillService;
        this.mapper = mapper;
    }

    // Submit a new application
    @PostMapping
    public ResponseEntity<ApplicationDTO> submitApplication(@RequestBody ApplicationCreateDTO applicationCreateDTO) {
        // Get the job
        Job job = jobService.getJobById(applicationCreateDTO.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                  "Job not found with ID: " + applicationCreateDTO.getJobId()));
        
        // Convert DTO to JobSeeker entity
        JobSeeker jobSeeker = mapper.toJobSeekerEntity(applicationCreateDTO.getJobSeeker());
        
        // Check if job seeker with email already exists
        if (jobSeeker.getEmail() != null) {
            jobSeekerService.findByEmail(jobSeeker.getEmail())
                .ifPresent(existingJobSeeker -> {
                    // Update existing job seeker with new info but keep the ID
                    jobSeeker.setId(existingJobSeeker.getId());
                    
                    // Keep existing skills and add new ones
                    Set<Skill> existingSkills = existingJobSeeker.getSkills();
                    if (existingSkills != null) {
                        jobSeeker.getSkills().addAll(existingSkills);
                    }
                });
        }
        
        // Process skills
        if (applicationCreateDTO.getJobSeeker().getSkills() != null && !applicationCreateDTO.getJobSeeker().getSkills().isEmpty()) {
            Set<Skill> skills = new HashSet<>();
            
            for (String skillName : applicationCreateDTO.getJobSeeker().getSkills()) {
                Skill skill = skillService.findByNameOrCreate(skillName);
                skills.add(skill);
            }
            
            jobSeeker.setSkills(skills);
        }
        
        // Create application entity
        Application application = new Application();
        application.setJob(job);
        application.setJobSeeker(jobSeeker);
        application.setStatus(applicationCreateDTO.getStatus());
        
        // Save the application
        Application savedApplication = applicationService.saveApplication(application);
        
        // Convert to DTO
        ApplicationDTO applicationDTO = mapper.toApplicationDTO(savedApplication);
        
        return new ResponseEntity<>(applicationDTO, HttpStatus.CREATED);
    }

    // Get all applications
    @GetMapping
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        List<Application> applications = applicationService.getAllApplications();
        
        List<ApplicationDTO> applicationDTOs = applications.stream()
                .map(mapper::toApplicationDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(applicationDTOs, HttpStatus.OK);
    }

    // Get application by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
        return applicationService.getApplicationById(id)
                .map(application -> {
                    ApplicationDTO applicationDTO = mapper.toApplicationDTO(application);
                    return new ResponseEntity<>(applicationDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get applications by job seeker ID
    @GetMapping("/jobseeker/{jobSeekerId}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByJobSeekerId(@PathVariable Long jobSeekerId) {
        List<Application> applications = applicationService.getApplicationsByJobSeekerId(jobSeekerId);
        
        List<ApplicationDTO> applicationDTOs = applications.stream()
                .map(mapper::toApplicationDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(applicationDTOs, HttpStatus.OK);
    }

    // Get applications by job ID
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByJobId(@PathVariable Long jobId) {
        List<Application> applications = applicationService.getApplicationsByJobId(jobId);
        
        List<ApplicationDTO> applicationDTOs = applications.stream()
                .map(mapper::toApplicationDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(applicationDTOs, HttpStatus.OK);
    }

    // Update application status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationDTO> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatusUpdateDTO statusUpdate) {
        
        return applicationService.getApplicationById(id)
                .map(application -> {
                    application.setStatus(statusUpdate.getStatus());
                    Application updatedApplication = applicationService.saveApplication(application);
                    
                    ApplicationDTO applicationDTO = mapper.toApplicationDTO(updatedApplication);
                    return new ResponseEntity<>(applicationDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Delete an application
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        return applicationService.getApplicationById(id)
                .map(application -> {
                    applicationService.deleteApplication(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}