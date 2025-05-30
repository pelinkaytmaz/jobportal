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

import com.dauphine.jobportal.dto.CompanyCreateDTO;
import com.dauphine.jobportal.dto.CompanyDTO;
import com.dauphine.jobportal.model.Company;
import com.dauphine.jobportal.service.CompanyService;
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
@RequestMapping("/api/companies")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Entreprises", description = "API de gestion des entreprises du portail d'emploi")
@SecurityRequirement(name = "JWT")
public class CompanyController {

    private final CompanyService companyService;
    private final EntityDTOMapper mapper;

    @Autowired
    public CompanyController(CompanyService companyService, EntityDTOMapper mapper) {
        this.companyService = companyService;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(
        summary = "Créer une nouvelle entreprise",
        description = "Permet d'ajouter une nouvelle entreprise au système avec ses informations de base (nom, description, localisation, site web)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Entreprise créée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CompanyDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "id": 1,
                      "name": "TechCorp",
                      "description": "Entreprise leader en technologie",
                      "location": "Paris, France",
                      "website": "https://techcorp.com"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données d'entreprise invalides",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "error": "Bad Request",
                      "message": "Le nom de l'entreprise est requis"
                    }
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
    public ResponseEntity<CompanyDTO> createCompany(
        @Parameter(
            description = "Informations de l'entreprise à créer",
            required = true,
            schema = @Schema(implementation = CompanyCreateDTO.class)
        )
        @RequestBody CompanyCreateDTO companyCreateDTO) {
        
        Company company = mapper.toCompanyEntity(companyCreateDTO);
        Company savedCompany = companyService.saveCompany(company);
        
        CompanyDTO companyDTO = mapper.toCompanyDTO(savedCompany);
        return new ResponseEntity<>(companyDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
        summary = "Récupérer toutes les entreprises",
        description = "Retourne la liste complète de toutes les entreprises enregistrées dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste des entreprises récupérée avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CompanyDTO.class),
                examples = @ExampleObject(
                    value = """
                    [
                      {
                        "id": 1,
                        "name": "TechCorp",
                        "description": "Entreprise leader en technologie",
                        "location": "Paris, France",
                        "website": "https://techcorp.com"
                      },
                      {
                        "id": 2,
                        "name": "StartupInc",
                        "description": "Startup innovante",
                        "location": "Lyon, France",
                        "website": "https://startupinc.fr"
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
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        
        List<CompanyDTO> companyDTOs = companies.stream()
                .map(mapper::toCompanyDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(companyDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer une entreprise par son ID",
        description = "Retourne les détails d'une entreprise spécifique basée sur son identifiant unique"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Entreprise trouvée",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CompanyDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "id": 1,
                      "name": "TechCorp",
                      "description": "Entreprise leader en technologie",
                      "location": "Paris, France",
                      "website": "https://techcorp.com"
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
                      "message": "Entreprise avec l'ID 999 non trouvée"
                    }
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
    public ResponseEntity<CompanyDTO> getCompanyById(
        @Parameter(
            description = "ID unique de l'entreprise",
            required = true,
            example = "1"
        )
        @PathVariable Long id) {
        
        return companyService.getCompanyById(id)
                .map(company -> {
                    CompanyDTO companyDTO = mapper.toCompanyDTO(company);
                    return new ResponseEntity<>(companyDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour une entreprise",
        description = "Met à jour toutes les informations d'une entreprise existante. Tous les champs sont remplacés par les nouvelles valeurs."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Entreprise mise à jour avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CompanyDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "id": 1,
                      "name": "TechCorp Updated",
                      "description": "Description mise à jour",
                      "location": "Nice, France",
                      "website": "https://techcorp-new.com"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Entreprise introuvable",
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
    public ResponseEntity<CompanyDTO> updateCompany(
        @Parameter(
            description = "ID de l'entreprise à mettre à jour",
            required = true,
            example = "1"
        )
        @PathVariable Long id,
        @Parameter(
            description = "Nouvelles informations de l'entreprise",
            required = true,
            schema = @Schema(implementation = CompanyCreateDTO.class)
        )
        @RequestBody CompanyCreateDTO companyCreateDTO) {
        
        return companyService.getCompanyById(id)
                .map(existingCompany -> {
                    existingCompany.setName(companyCreateDTO.getName());
                    existingCompany.setDescription(companyCreateDTO.getDescription());
                    existingCompany.setLocation(companyCreateDTO.getLocation());
                    existingCompany.setWebsite(companyCreateDTO.getWebsite());
                    
                    Company updatedCompany = companyService.saveCompany(existingCompany);
                    CompanyDTO companyDTO = mapper.toCompanyDTO(updatedCompany);
                    
                    return new ResponseEntity<>(companyDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer une entreprise",
        description = "Supprime définitivement une entreprise du système. Cette action est irréversible."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Entreprise supprimée avec succès"
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
                      "message": "Entreprise avec l'ID 999 non trouvée"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non autorisé - Token JWT requis",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflit - Impossible de supprimer (entreprise liée à des offres d'emploi)",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteCompany(
        @Parameter(
            description = "ID de l'entreprise à supprimer",
            required = true,
            example = "1"
        )
        @PathVariable Long id) {
        
        return companyService.getCompanyById(id)
                .map(company -> {
                    companyService.deleteCompany(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    @Operation(
        summary = "Rechercher des entreprises par nom",
        description = "Effectue une recherche textuelle sur les noms d'entreprises. La recherche peut être partielle et n'est pas sensible à la casse."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Résultats de recherche récupérés avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CompanyDTO.class),
                examples = @ExampleObject(
                    value = """
                    [
                      {
                        "id": 1,
                        "name": "TechCorp",
                        "description": "Entreprise leader en technologie",
                        "location": "Paris, France",
                        "website": "https://techcorp.com"
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Paramètre de recherche manquant ou invalide",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "error": "Bad Request",
                      "message": "Le paramètre 'name' est requis pour la recherche"
                    }
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
    public ResponseEntity<List<CompanyDTO>> searchCompaniesByName(
        @Parameter(
            description = "Nom ou partie du nom de l'entreprise à rechercher",
            required = true,
            example = "Tech"
        )
        @RequestParam String name) {
        
        List<Company> companies = companyService.findCompaniesByName(name);
        
        List<CompanyDTO> companyDTOs = companies.stream()
                .map(mapper::toCompanyDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(companyDTOs, HttpStatus.OK);
    }
}