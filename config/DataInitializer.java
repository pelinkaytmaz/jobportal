package com.dauphine.jobportal.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.dauphine.jobportal.model.Role;
import com.dauphine.jobportal.model.enums.ERole;
import com.dauphine.jobportal.repository.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initRoles();
    }
    
    private void initRoles() {
        if (roleRepository.count() == 0) {
            Arrays.asList(ERole.values()).forEach(role -> {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
            });
        }
    }
}