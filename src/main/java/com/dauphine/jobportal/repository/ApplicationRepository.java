package com.dauphine.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dauphine.jobportal.model.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    // Find applications by job ID
    List<Application> findByJobId(Long jobId);
    
    // Find applications by job seeker ID
    List<Application> findByJobSeekerId(Long jobSeekerId);
}