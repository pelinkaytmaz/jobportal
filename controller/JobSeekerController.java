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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dauphine.jobportal.dto.JobSeekerCreateDTO;
import com.dauphine.jobportal.dto.JobSeekerDTO;
import com.dauphine.jobportal.model.JobSeeker;
import com.dauphine.jobportal.model.Skill;
import com.dauphine.jobportal.service.JobSeekerService;
import com.dauphine.jobportal.service.SkillService;
import com.dauphine.jobportal.util.EntityDTOMapper;

@RestController
@RequestMapping("/api/jobseekers")
@CrossOrigin(origins = "http://localhost:4200")
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;
    private final SkillService skillService;
    private final EntityDTOMapper mapper;

    @Autowired
    public JobSeekerController(JobSeekerService jobSeekerService, 
                              SkillService skillService,
                              EntityDTOMapper mapper) {
        this.jobSeekerService = jobSeekerService;
        this.skillService = skillService;
        this.mapper = mapper;
    }

    // Create a new job seeker
    @PostMapping
    public ResponseEntity<JobSeekerDTO> createJobSeeker(@RequestBody JobSeekerCreateDTO jobSeekerCreateDTO) {
        // Convert DTO to entity
        JobSeeker jobSeeker = mapper.toJobSeekerEntity(jobSeekerCreateDTO);
        
        // Process skills
        if (jobSeekerCreateDTO.getSkills() != null && !jobSeekerCreateDTO.getSkills().isEmpty()) {
            Set<Skill> skills = new HashSet<>();
            
            for (String skillName : jobSeekerCreateDTO.getSkills()) {
                Skill skill = skillService.findByNameOrCreate(skillName);
                skills.add(skill);
            }
            
            jobSeeker.setSkills(skills);
        }
        
        // Save job seeker
        JobSeeker savedJobSeeker = jobSeekerService.saveJobSeeker(jobSeeker);
        
        // Convert to DTO
        JobSeekerDTO jobSeekerDTO = mapper.toJobSeekerDTO(savedJobSeeker);
        
        return new ResponseEntity<>(jobSeekerDTO, HttpStatus.CREATED);
    }

    // Get all job seekers
    @GetMapping
    public ResponseEntity<List<JobSeekerDTO>> getAllJobSeekers() {
        List<JobSeeker> jobSeekers = jobSeekerService.getAllJobSeekers();
        
        List<JobSeekerDTO> jobSeekerDTOs = jobSeekers.stream()
                .map(mapper::toJobSeekerDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(jobSeekerDTOs, HttpStatus.OK);
    }

    // Get job seeker by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobSeekerDTO> getJobSeekerById(@PathVariable Long id) {
        return jobSeekerService.getJobSeekerById(id)
                .map(jobSeeker -> {
                    JobSeekerDTO jobSeekerDTO = mapper.toJobSeekerDTO(jobSeeker);
                    return new ResponseEntity<>(jobSeekerDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a job seeker
    @PutMapping("/{id}")
    public ResponseEntity<JobSeekerDTO> updateJobSeeker(@PathVariable Long id, @RequestBody JobSeekerCreateDTO jobSeekerCreateDTO) {
        return jobSeekerService.getJobSeekerById(id)
                .map(existingJobSeeker -> {
                    // Update basic info
                    existingJobSeeker.setFirstName(jobSeekerCreateDTO.getFirstName());
                    existingJobSeeker.setLastName(jobSeekerCreateDTO.getLastName());
                    existingJobSeeker.setEmail(jobSeekerCreateDTO.getEmail());
                    existingJobSeeker.setPhoneNumber(jobSeekerCreateDTO.getPhoneNumber());
                    
                    // Update experiences
                    if (jobSeekerCreateDTO.getExperiences() != null && !jobSeekerCreateDTO.getExperiences().isEmpty()) {
                        // Clear existing experiences
                        existingJobSeeker.getExperiences().clear();
                        
                        // Add new experiences from DTO
                        jobSeekerCreateDTO.getExperiences().forEach(expDTO -> {
                            existingJobSeeker.addExperience(mapper.toExperienceEntity(expDTO));
                        });
                    }
                    
                    // Update skills
                    if (jobSeekerCreateDTO.getSkills() != null && !jobSeekerCreateDTO.getSkills().isEmpty()) {
                        Set<Skill> skills = new HashSet<>();
                        
                        for (String skillName : jobSeekerCreateDTO.getSkills()) {
                            Skill skill = skillService.findByNameOrCreate(skillName);
                            skills.add(skill);
                        }
                        
                        existingJobSeeker.getSkills().clear();
                        existingJobSeeker.getSkills().addAll(skills);
                    }
                    
                    // Save updated job seeker
                    JobSeeker updatedJobSeeker = jobSeekerService.saveJobSeeker(existingJobSeeker);
                    
                    // Convert to DTO
                    JobSeekerDTO jobSeekerDTO = mapper.toJobSeekerDTO(updatedJobSeeker);
                    
                    return new ResponseEntity<>(jobSeekerDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Delete a job seeker
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobSeeker(@PathVariable Long id) {
        return jobSeekerService.getJobSeekerById(id)
                .map(jobSeeker -> {
                    jobSeekerService.deleteJobSeeker(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Find job seeker by email
    @GetMapping("/email/{email}")
    public ResponseEntity<JobSeekerDTO> findJobSeekerByEmail(@PathVariable String email) {
        return jobSeekerService.findByEmail(email)
                .map(jobSeeker -> {
                    JobSeekerDTO jobSeekerDTO = mapper.toJobSeekerDTO(jobSeeker);
                    return new ResponseEntity<>(jobSeekerDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}