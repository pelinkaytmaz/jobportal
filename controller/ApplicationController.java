package com.dauphine.jobportal.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Applications", description = "API de gestion des candidatures")
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

        @PostMapping
        @Operation(summary = "Soumettre une nouvelle candidature", description = "Permet à un candidat de postuler à une offre d'emploi. Si le candidat existe déjà (basé sur l'email), ses informations sont mises à jour.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Candidature créée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Offre d'emploi ou candidat introuvable", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Données de candidature invalides", content = @Content)
        })
        public ResponseEntity<ApplicationDTO> submitApplication(
                        @Parameter(description = "Données de la candidature à créer", required = true) @RequestBody ApplicationCreateDTO applicationCreateDTO) {

                // Retrieve the job offer
                Job job = jobService.getJobById(applicationCreateDTO.getJobId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Job not found with ID: " + applicationCreateDTO.getJobId()));

                // Retrieve the job seeker by ID
                JobSeeker currentJobSeeker = jobSeekerService
                                .getJobSeekerById(applicationCreateDTO.getJobSeeker().getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "JobSeeker not found with ID: "
                                                                + applicationCreateDTO.getJobSeeker().getId()));

                // Check if a job seeker already exists with this email and merge skills if
                // needed
                String email = currentJobSeeker.getUser() != null ? currentJobSeeker.getUser().getEmail() : null;
                if (email != null) {
                        jobSeekerService.findByEmail(email).ifPresent(existingJobSeeker -> {
                                currentJobSeeker.setId(existingJobSeeker.getId());

                                if (existingJobSeeker.getSkills() != null) {
                                        currentJobSeeker.getSkills().addAll(existingJobSeeker.getSkills());
                                }
                        });
                }

                // Process incoming skills
                if (applicationCreateDTO.getJobSeeker().getSkills() != null &&
                                !applicationCreateDTO.getJobSeeker().getSkills().isEmpty()) {

                        Set<Skill> skills = applicationCreateDTO.getJobSeeker().getSkills().stream()
                                        .map(skillDTO -> skillService.findByNameOrCreate(skillDTO.getName()))
                                        .collect(Collectors.toSet());

                        currentJobSeeker.setSkills(skills);
                }

                // Create and save the application
                Application application = new Application();
                application.setJob(job);
                application.setJobSeeker(currentJobSeeker);
                application.setStatus(applicationCreateDTO.getStatus());

                Application savedApplication = applicationService.saveApplication(application);
                ApplicationDTO applicationDTO = mapper.toApplicationDTO(savedApplication);

                return new ResponseEntity<>(applicationDTO, HttpStatus.CREATED);
        }

        @GetMapping
        @Transactional(readOnly = true)  // ← Ajout de @Transactional
        @Operation(summary = "Récupérer toutes les candidatures", description = "Retourne la liste complète de toutes les candidatures enregistrées dans le système")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste des candidatures récupérée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationDTO.class)))
        })
        public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
                List<Application> applications = applicationService.getAllApplications();

                List<ApplicationDTO> applicationDTOs = applications.stream()
                                .map(mapper::toApplicationDTO)
                                .collect(Collectors.toList());

                return new ResponseEntity<>(applicationDTOs, HttpStatus.OK);
        }

        @GetMapping("/{id}")
        @Transactional(readOnly = true)  // ← Ajout de @Transactional
        @Operation(summary = "Récupérer une candidature par son ID", description = "Retourne les détails d'une candidature spécifique basée sur son identifiant unique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Candidature trouvée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Candidature introuvable", content = @Content)
        })
        public ResponseEntity<ApplicationDTO> getApplicationById(
                        @Parameter(description = "ID de la candidature", required = true) @PathVariable Long id) {

                return applicationService.getApplicationById(id)
                                .map(application -> {
                                        ApplicationDTO applicationDTO = mapper.toApplicationDTO(application);
                                        return new ResponseEntity<>(applicationDTO, HttpStatus.OK);
                                })
                                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        @GetMapping("/jobseeker/{jobSeekerId}")
        @Transactional(readOnly = true)  // ← Ajout de @Transactional
        @Operation(summary = "Récupérer les candidatures d'un candidat", description = "Retourne toutes les candidatures soumises par un candidat spécifique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Candidatures du candidat récupérées avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Candidat introuvable", content = @Content)
        })
        public ResponseEntity<List<ApplicationDTO>> getApplicationsByJobSeekerId(
                        @Parameter(description = "ID du candidat", required = true) @PathVariable Long jobSeekerId) {

                List<Application> applications = applicationService.getApplicationsByJobSeekerId(jobSeekerId);

                List<ApplicationDTO> applicationDTOs = applications.stream()
                                .map(mapper::toApplicationDTO)
                                .collect(Collectors.toList());

                return new ResponseEntity<>(applicationDTOs, HttpStatus.OK);
        }

        @GetMapping("/job/{jobId}")
        @Transactional(readOnly = true)  // ← Ajout de @Transactional
        @Operation(summary = "Récupérer les candidatures pour une offre d'emploi", description = "Retourne toutes les candidatures reçues pour une offre d'emploi spécifique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Candidatures pour l'offre récupérées avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Offre d'emploi introuvable", content = @Content)
        })
        public ResponseEntity<List<ApplicationDTO>> getApplicationsByJobId(
                        @Parameter(description = "ID de l'offre d'emploi", required = true) @PathVariable Long jobId) {

                List<Application> applications = applicationService.getApplicationsByJobId(jobId);

                List<ApplicationDTO> applicationDTOs = applications.stream()
                                .map(mapper::toApplicationDTO)
                                .collect(Collectors.toList());

                return new ResponseEntity<>(applicationDTOs, HttpStatus.OK);
        }

        @PatchMapping("/{id}/status")
        @Operation(summary = "Mettre à jour le statut d'une candidature", description = "Permet de modifier le statut d'une candidature (ex: EN_ATTENTE, ACCEPTEE, REJETEE)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Statut de la candidature mis à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Candidature introuvable", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Statut invalide", content = @Content)
        })
        public ResponseEntity<ApplicationDTO> updateApplicationStatus(
                        @Parameter(description = "ID de la candidature", required = true) @PathVariable Long id,
                        @Parameter(description = "Nouveau statut de la candidature", required = true) @RequestBody ApplicationStatusUpdateDTO statusUpdate) {

                return applicationService.getApplicationById(id)
                                .map(application -> {
                                        application.setStatus(statusUpdate.getStatus());
                                        Application updatedApplication = applicationService
                                                        .saveApplication(application);

                                        ApplicationDTO applicationDTO = mapper.toApplicationDTO(updatedApplication);
                                        return new ResponseEntity<>(applicationDTO, HttpStatus.OK);
                                })
                                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Supprimer une candidature", description = "Supprime définitivement une candidature du système")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Candidature supprimée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Candidature introuvable", content = @Content)
        })
        public ResponseEntity<Void> deleteApplication(
                        @Parameter(description = "ID de la candidature à supprimer", required = true) @PathVariable Long id) {

                return applicationService.getApplicationById(id)
                                .map(application -> {
                                        applicationService.deleteApplication(id);
                                        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                                })
                                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
}