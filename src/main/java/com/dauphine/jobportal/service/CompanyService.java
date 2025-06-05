package com.dauphine.jobportal.service;

import com.dauphine.jobportal.model.Company;
import com.dauphine.jobportal.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Save a company
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    // Get all companies
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    // Get company by ID
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    // Delete a company
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    // Find companies by name containing
    public List<Company> findCompaniesByName(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name);
    }
}