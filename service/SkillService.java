package com.dauphine.jobportal.service;

import com.dauphine.jobportal.model.Skill;
import com.dauphine.jobportal.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // Save a skill
    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    // Get all skills
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    // Get skill by ID
    public Optional<Skill> getSkillById(Long id) {
        return skillRepository.findById(id);
    }

    // Delete a skill
    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    // Find skill by name
    public Optional<Skill> findByName(String name) {
        return skillRepository.findByNameIgnoreCase(name);
    }
    
    // Find skill by name or create if not exists
    @Transactional
    public Skill findByNameOrCreate(String name) {
        Optional<Skill> existingSkill = skillRepository.findByNameIgnoreCase(name);
        
        if (existingSkill.isPresent()) {
            return existingSkill.get();
        } else {
            Skill newSkill = new Skill();
            newSkill.setName(name);
            return skillRepository.save(newSkill);
        }
    }
}