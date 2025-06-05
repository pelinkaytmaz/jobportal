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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Offres d'emploi", description = "API de gestion des offres d'emploi")
@SecurityRequirement(name = "JWT")
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

    @PostMapping
    @Operation(
        summary = "Créer une nouvelle offre d'emploi",
        description = "Permet à une entreprise de publier une nouvelle offre d'emploi avec tous les détails nécessaires " +
                     "(titre, description, responsabilités, qualifications requises, localisation, salaire, type de contrat)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Offre d'emploi créée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JobDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "id": 1,
                      "title": "Développeur Full Stack",
                      "description": "Nous recherchons un développeur passionné...",
                      "responsibilities": "Développement d'applications web, maintenance...",
                      "qualifications": "Bac+5, 3 ans d'expérience en Java/Angular...",
                      "location": "Paris, France",
                      "salaryMin": 45000.0,
                      "salaryMax": 60000.0,
                      "type": "FULL_TIME",
                      "experienceLevel": "MID_LEVEL",
                      "company": {
                        "id": 1,
                        "name": "TechCorp"
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Entreprise introuvable",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "error": "Not Found",
                      "message": "Company not found with ID: 999"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données d'offre invalides",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non autorisé - Token JWT requis",
            content = @Content
        )
    })
    public ResponseEntity<JobDTO> createJob(
        @Parameter(
            description = "Informations de l'offre d'emploi à créer",
            required = true,
            schema = @Schema(implementation = JobCreateDTO.class)
        )
        @RequestBody JobCreateDTO jobCreateDTO) {
        
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

    @GetMapping
    @Operation(
        summary = "Récupérer toutes les offres d'emploi",
        description = "Retourne la liste complète de toutes les offres d'emploi disponibles sur la plateforme"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste des offres d'emploi récupérée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JobDTO.class),
                examples = @ExampleObject(
                    value = """
                    [
                      {
                        "id": 1,
                        "title": "Développeur Full Stack",
                        "description": "Nous recherchons un développeur passionné...",
                        "location": "Paris, France",
                        "salaryMin": 45000.0,
                        "salaryMax": 60000.0,
                        "type": "FULL_TIME",
                        "experienceLevel": "MID_LEVEL",
                        "company": {
                          "id": 1,
                          "name": "TechCorp"
                        }
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non autorisé - Token JWT requis",
            content = @Content
        )
    })
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();

        // Convert list of entities to list of DTOs
        List<JobDTO> jobDTOs = jobs.stream()
                .map(mapper::toJobDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(jobDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer une offre d'emploi par son ID",
        description = "Retourne les détails complets d'une offre d'emploi spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Offre d'emploi trouvée",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JobDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Offre d'emploi introuvable",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non autorisé - Token JWT requis",
            content = @Content
        )
    })
    public ResponseEntity<JobDTO> getJobById(
        @Parameter(
            description = "ID unique de l'offre d'emploi",
            required = true,
            example = "1"
        )
        @PathVariable Long id) {
        
        return jobService.getJobById(id)
                .map(job -> {
                    JobDTO jobDTO = mapper.toJobDTO(job);
                    return new ResponseEntity<>(jobDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour une offre d'emploi",
        description = "Met à jour toutes les informations d'une offre d'emploi existante. " +
                     "L'entreprise associée peut également être modifiée."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Offre d'emploi mise à jour avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JobDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Offre d'emploi ou entreprise introuvable",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données invalides",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non autorisé - Token JWT requis",
            content = @Content
        )
    })
    public ResponseEntity<JobDTO> updateJob(
        @Parameter(
            description = "ID de l'offre d'emploi à mettre à jour",
            required = true,
            example = "1"
        )
        @PathVariable Long id,
        @Parameter(
            description = "Nouvelles informations de l'offre d'emploi",
            required = true,
            schema = @Schema(implementation = JobCreateDTO.class)
        )
        @RequestBody JobCreateDTO jobCreateDTO) {
        
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

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer une offre d'emploi",
        description = "Supprime définitivement une offre d'emploi du système. " +
                     "Toutes les candidatures associées seront également supprimées."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Offre d'emploi supprimée avec succès"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Offre d'emploi introuvable",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non autorisé - Token JWT requis",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflit - Impossible de supprimer (candidatures en cours)",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteJob(
        @Parameter(
            description = "ID de l'offre d'emploi à supprimer",
            required = true,
            example = "1"
        )
        @PathVariable Long id) {
        
        return jobService.getJobById(id)
                .map(job -> {
                    jobService.deleteJob(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    @Operation(
        summary = "Rechercher des offres d'emploi avec filtres",
        description = "Effectue une recherche avancée d'offres d'emploi avec de multiples critères de filtrage. " +
                     "Tous les paramètres sont optionnels et peuvent être combinés pour affiner les résultats."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Résultats de recherche récupérés avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JobDTO.class),
                examples = @ExampleObject(
                    value = """
                    [
                      {
                        "id": 1,
                        "title": "Développeur Java",
                        "description": "Poste de développeur Java...",
                        "location": "Paris, France",
                        "salaryMin": 45000.0,
                        "salaryMax": 60000.0,
                        "type": "FULL_TIME",
                        "experienceLevel": "MID_LEVEL",
                        "company": {
                          "id": 1,
                          "name": "TechCorp"
                        }
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Paramètres de recherche invalides",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non autorisé - Token JWT requis",
            content = @Content
        )
    })
    public ResponseEntity<List<JobDTO>> searchJobs(
        @Parameter(
            description = "Mot-clé pour rechercher dans le titre et la description",
            example = "Java"
        )
        @RequestParam(required = false) String keyword,
        
        @Parameter(
            description = "Localisation géographique du poste",
            example = "Paris"
        )
        @RequestParam(required = false) String location,
        
        @Parameter(
            description = "Type de contrat",
            schema = @Schema(implementation = JobType.class),
            example = "FULL_TIME"
        )
        @RequestParam(required = false) JobType type,
        
        @Parameter(
            description = "Niveau d'expérience requis",
            schema = @Schema(implementation = ExperienceLevel.class),
            example = "MID_LEVEL"
        )
        @RequestParam(required = false) ExperienceLevel experienceLevel,
        
        @Parameter(
            description = "Salaire minimum souhaité (en euros)",
            example = "40000"
        )
        @RequestParam(required = false) Double minSalary,
        
        @Parameter(
            description = "Salaire maximum souhaité (en euros)",
            example = "70000"
        )
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