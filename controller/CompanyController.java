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

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

    private final CompanyService companyService;
    private final EntityDTOMapper mapper;

    @Autowired
    public CompanyController(CompanyService companyService, EntityDTOMapper mapper) {
        this.companyService = companyService;
        this.mapper = mapper;
    }

    // Create a new company
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyCreateDTO companyCreateDTO) {
        Company company = mapper.toCompanyEntity(companyCreateDTO);
        Company savedCompany = companyService.saveCompany(company);
        
        CompanyDTO companyDTO = mapper.toCompanyDTO(savedCompany);
        return new ResponseEntity<>(companyDTO, HttpStatus.CREATED);
    }

    // Get all companies
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        
        List<CompanyDTO> companyDTOs = companies.stream()
                .map(mapper::toCompanyDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(companyDTOs, HttpStatus.OK);
    }

    // Get company by ID
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id)
                .map(company -> {
                    CompanyDTO companyDTO = mapper.toCompanyDTO(company);
                    return new ResponseEntity<>(companyDTO, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a company
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @RequestBody CompanyCreateDTO companyCreateDTO) {
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

    // Delete a company
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        return companyService.getCompanyById(id)
                .map(company -> {
                    companyService.deleteCompany(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Search companies by name
    @GetMapping("/search")
    public ResponseEntity<List<CompanyDTO>> searchCompaniesByName(@RequestParam String name) {
        List<Company> companies = companyService.findCompaniesByName(name);
        
        List<CompanyDTO> companyDTOs = companies.stream()
                .map(mapper::toCompanyDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(companyDTOs, HttpStatus.OK);
    }
}