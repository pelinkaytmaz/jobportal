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
@RequestMapping("/api/jobseekers")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Candidats", description = "API de gestion des profils candidats et demandeurs d'emploi")
@SecurityRequirement(name = "JWT")
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

        /*
         * @PostMapping
         * 
         * @Operation(summary = "Créer un nouveau profil candidat", description =
         * "Permet de créer un nouveau profil de candidat avec ses informations personnelles, "
         * +
         * "expériences professionnelles et compétences. Les compétences sont automatiquement "
         * +
         * "créées si elles n'existent pas déjà.")
         * 
         * @ApiResponses(value = {
         * 
         * @ApiResponse(responseCode = "201", description =
         * "Profil candidat créé avec succès", content = @Content(mediaType =
         * "application/json", schema = @Schema(implementation = JobSeekerDTO.class),
         * examples = @ExampleObject(value = """
         * {
         * "id": 1,
         * "firstName": "Jean",
         * "lastName": "Dupont",
         * "email": "jean.dupont@email.com",
         * "phoneNumber": "+33 6 12 34 56 78",
         * "skills": ["Java", "Spring Boot", "Angular", "MySQL"],
         * "experiences": [
         * {
         * "id": 1,
         * "jobTitle": "Développeur Java",
         * "company": "TechCorp",
         * "startDate": "2020-01-15",
         * "endDate": "2023-06-30",
         * "description": "Développement d'applications web en Java/Spring"
         * }
         * ]
         * }
         * """))),
         * 
         * @ApiResponse(responseCode = "400", description =
         * "Données candidat invalides", content = @Content(mediaType =
         * "application/json", examples = @ExampleObject(value = """
         * {
         * "error": "Bad Request",
         * "message": "L'email est requis et doit être valide"
         * }
         * """))),
         * 
         * @ApiResponse(responseCode = "409", description = "Email déjà utilisé",
         * content = @Content(mediaType = "application/json", examples
         * = @ExampleObject(value = """
         * {
         * "error": "Conflict",
         * "message": "Un candidat avec cet email existe déjà"
         * }
         * """))),
         * 
         * @ApiResponse(responseCode = "401", description =
         * "Non autorisé - Token JWT requis", content = @Content)
         * })
         * public ResponseEntity<JobSeekerDTO> createJobSeeker(
         * 
         * @Parameter(description = "Informations du profil candidat à créer", required
         * = true, schema = @Schema(implementation =
         * JobSeekerCreateDTO.class)) @RequestBody JobSeekerDTO jobSeekerCreateDTO) {
         * 
         * // Convert DTO to entity
         * JobSeeker jobSeeker = mapper.toJobSeekerEntity(jobSeekerCreateDTO);
         * 
         * // Process skills
         * if (jobSeekerCreateDTO.getSkills() != null &&
         * !jobSeekerCreateDTO.getSkills().isEmpty()) {
         * Set<Skill> skills = new HashSet<>();
         * 
         * for (String skillName : jobSeekerCreateDTO.getSkills()) {
         * Skill skill = skillService.findByNameOrCreate(skillName);
         * skills.add(skill);
         * }
         * 
         * jobSeeker.setSkills(skills);
         * }
         * 
         * // Save job seeker
         * JobSeeker savedJobSeeker = jobSeekerService.saveJobSeeker(jobSeeker);
         * 
         * // Convert to DTO
         * JobSeekerDTO jobSeekerDTO = mapper.toJobSeekerDTO(savedJobSeeker);
         * 
         * return new ResponseEntity<>(jobSeekerDTO, HttpStatus.CREATED);
         * }
         */
        @GetMapping
        @Operation(summary = "Récupérer tous les candidats", description = "Retourne la liste complète de tous les profils candidats enregistrés dans le système. "
                        +
                        "Utile pour les recruteurs pour consulter la base de candidats.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste des candidats récupérée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobSeekerDTO.class), examples = @ExampleObject(value = """
                                        [
                                          {
                                            "id": 1,
                                            "firstName": "Jean",
                                            "lastName": "Dupont",
                                            "email": "jean.dupont@email.com",
                                            "phoneNumber": "+33 6 12 34 56 78",
                                            "skills": ["Java", "Spring Boot", "Angular"],
                                            "experiences": []
                                          },
                                          {
                                            "id": 2,
                                            "firstName": "Marie",
                                            "lastName": "Martin",
                                            "email": "marie.martin@email.com",
                                            "phoneNumber": "+33 6 98 76 54 32",
                                            "skills": ["Python", "Django", "React"],
                                            "experiences": []
                                          }
                                        ]
                                        """))),
                        @ApiResponse(responseCode = "401", description = "Non autorisé - Token JWT requis", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Accès interdit - Rôle recruteur requis", content = @Content)
        })
        public ResponseEntity<List<JobSeekerDTO>> getAllJobSeekers() {
                List<JobSeeker> jobSeekers = jobSeekerService.getAllJobSeekers();

                List<JobSeekerDTO> jobSeekerDTOs = jobSeekers.stream()
                                .map(mapper::toJobSeekerDTO)
                                .collect(Collectors.toList());

                return new ResponseEntity<>(jobSeekerDTOs, HttpStatus.OK);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Récupérer un candidat par son ID", description = "Retourne les détails complets d'un profil candidat spécifique, incluant "
                        +
                        "ses expériences professionnelles et compétences")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Profil candidat trouvé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobSeekerDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Candidat introuvable", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "error": "Not Found",
                                          "message": "Candidat avec l'ID 999 non trouvé"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "401", description = "Non autorisé - Token JWT requis", content = @Content)
        })
        public ResponseEntity<JobSeekerDTO> getJobSeekerById(
                        @Parameter(description = "ID unique du candidat", required = true, example = "1") @PathVariable Long id) {

                return jobSeekerService.getJobSeekerById(id)
                                .map(jobSeeker -> {
                                        JobSeekerDTO jobSeekerDTO = mapper.toJobSeekerDTO(jobSeeker);
                                        return new ResponseEntity<>(jobSeekerDTO, HttpStatus.OK);
                                })
                                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Mettre à jour un profil candidat", description = "Met à jour toutes les informations d'un profil candidat existant. "
                        +
                        "Les expériences et compétences existantes sont remplacées par les nouvelles valeurs.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Profil candidat mis à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobSeekerDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Candidat introuvable", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Email déjà utilisé par un autre candidat", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non autorisé - Token JWT requis", content = @Content)
        })
        public ResponseEntity<JobSeekerDTO> updateJobSeeker(
                        @Parameter(description = "ID du candidat à mettre à jour", required = true, example = "1") @PathVariable Long id,
                        @Parameter(description = "Nouvelles informations du profil candidat", required = true, schema = @Schema(implementation = JobSeekerCreateDTO.class)) @RequestBody JobSeekerCreateDTO jobSeekerCreateDTO) {

                return jobSeekerService.getJobSeekerById(id)
                                .map(existingJobSeeker -> {
                                        // Update basic info
                                        existingJobSeeker.setFirstName(jobSeekerCreateDTO.getFirstName());
                                        existingJobSeeker.setLastName(jobSeekerCreateDTO.getLastName());
                                        existingJobSeeker.setPhoneNumber(jobSeekerCreateDTO.getPhoneNumber());

                                        // Update experiences
                                        if (jobSeekerCreateDTO.getExperiences() != null
                                                        && !jobSeekerCreateDTO.getExperiences().isEmpty()) {
                                                // Clear existing experiences
                                                existingJobSeeker.getExperiences().clear();

                                                // Add new experiences from DTO
                                                jobSeekerCreateDTO.getExperiences().forEach(expDTO -> {
                                                        existingJobSeeker.addExperience(
                                                                        mapper.toExperienceEntity(expDTO));
                                                });
                                        }

                                        // Update skills
                                        if (jobSeekerCreateDTO.getSkills() != null
                                                        && !jobSeekerCreateDTO.getSkills().isEmpty()) {
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

        @DeleteMapping("/{id}")
        @Operation(summary = "Supprimer un profil candidat", description = "Supprime définitivement un profil candidat du système. "
                        +
                        "Toutes les candidatures associées seront également supprimées. Cette action est irréversible.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Profil candidat supprimé avec succès"),
                        @ApiResponse(responseCode = "404", description = "Candidat introuvable", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non autorisé - Token JWT requis", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Conflit - Impossible de supprimer (candidatures en cours)", content = @Content)
        })
        public ResponseEntity<Void> deleteJobSeeker(
                        @Parameter(description = "ID du candidat à supprimer", required = true, example = "1") @PathVariable Long id) {

                return jobSeekerService.getJobSeekerById(id)
                                .map(jobSeeker -> {
                                        jobSeekerService.deleteJobSeeker(id);
                                        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                                })
                                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        // @GetMapping("/email/{email}")
        // @Operation(summary = "Rechercher un candidat par email", description = "Permet de retrouver un profil candidat en utilisant son adresse email. "
        //                 +
        //                 "Utile pour vérifier l'existence d'un candidat ou récupérer ses informations.")
        // @ApiResponses(value = {
        //                 @ApiResponse(responseCode = "200", description = "Candidat trouvé par email", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobSeekerDTO.class), examples = @ExampleObject(value = """
        //                                 {
        //                                   "id": 1,
        //                                   "firstName": "Jean",
        //                                   "lastName": "Dupont",
        //                                   "email": "jean.dupont@email.com",
        //                                   "phoneNumber": "+33 6 12 34 56 78",
        //                                   "skills": ["Java", "Spring Boot"],
        //                                   "experiences": []
        //                                 }
        //                                 """))),
        //                 @ApiResponse(responseCode = "404", description = "Aucun candidat trouvé avec cet email", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        //                                 {
        //                                   "error": "Not Found",
        //                                   "message": "Aucun candidat trouvé avec l'email: inexistant@email.com"
        //                                 }
        //                                 """))),
        //                 @ApiResponse(responseCode = "400", description = "Format d'email invalide", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        //                                 {
        //                                   "error": "Bad Request",
        //                                   "message": "Format d'email invalide"
        //                                 }
        //                                 """))),
        //                 @ApiResponse(responseCode = "401", description = "Non autorisé - Token JWT requis", content = @Content)
        // })
        // public ResponseEntity<JobSeekerDTO> findJobSeekerByEmail(
        //                 @Parameter(description = "Adresse email du candidat à rechercher", required = true, example = "jean.dupont@email.com") @PathVariable String email) {

        //         return jobSeekerService.findByEmail(email)
        //                         .map(jobSeeker -> {
        //                                 JobSeekerDTO jobSeekerDTO = mapper.toJobSeekerDTO(jobSeeker);
        //                                 return new ResponseEntity<>(jobSeekerDTO, HttpStatus.OK);
        //                         })
        //                         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        // }
}