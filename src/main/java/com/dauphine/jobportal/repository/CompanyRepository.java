package com.dauphine.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dauphine.jobportal.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    // Find companies by name containing keyword
    List<Company> findByNameContainingIgnoreCase(String name);
}