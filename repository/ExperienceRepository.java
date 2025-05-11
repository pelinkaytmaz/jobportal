package com.dauphine.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dauphine.jobportal.model.Experience;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    
    // Find experiences by job seeker ID
    List<Experience> findByJobSeekerId(Long jobSeekerId);
}