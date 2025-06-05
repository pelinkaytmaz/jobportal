package com.dauphine.jobportal.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private String website;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Job> jobs = new HashSet<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    // Constructors
    public Company() {
    }

    public Company(String name, String description, String location, String website) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.website = website;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    // Utility methods for Jobs
    public void addJob(Job job) {
        jobs.add(job);
        job.setCompany(this);
    }

    public void removeJob(Job job) {
        jobs.remove(job);
        job.setCompany(null);
    }

    // Utility methods for Users
    public void addUser(User user) {
        users.add(user);
        user.setCompany(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setCompany(null);
    }

    // Business methods
    public int getJobCount() {
        return jobs != null ? jobs.size() : 0;
    }

    public int getUserCount() {
        return users != null ? users.size() : 0;
    }

    public boolean hasJobs() {
        return jobs != null && !jobs.isEmpty();
    }

    public boolean hasUsers() {
        return users != null && !users.isEmpty();
    }

    // Method to get all users with a specific role
    public Set<User> getUsersByRole(String roleName) {
        Set<User> usersWithRole = new HashSet<>();
        if (users != null) {
            for (User user : users) {
                if (user.getRoles().stream().anyMatch(role -> role.getName().name().equals(roleName))) {
                    usersWithRole.add(user);
                }
            }
        }
        return usersWithRole;
    }
}