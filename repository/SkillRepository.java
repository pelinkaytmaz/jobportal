package com.dauphine.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dauphine.jobportal.model.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    // Find skill by name (case insensitive)
    Optional<Skill> findByNameIgnoreCase(String name);
}