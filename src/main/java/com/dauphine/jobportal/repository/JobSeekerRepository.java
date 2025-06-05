package com.dauphine.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dauphine.jobportal.model.JobSeeker;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {
    @Query("SELECT DISTINCT js FROM JobSeeker js LEFT JOIN FETCH js.experiences LEFT JOIN FETCH js.skills LEFT JOIN FETCH js.applications")
    List<JobSeeker> findAllWithExperiences();

    @Query("SELECT DISTINCT js FROM JobSeeker js LEFT JOIN FETCH js.experiences LEFT JOIN FETCH js.skills WHERE js.id = :id")
    Optional<JobSeeker> findByIdWithExperiences(Long id);

    @Query("SELECT DISTINCT js FROM JobSeeker js " +
            "LEFT JOIN FETCH js.experiences " +
            "LEFT JOIN FETCH js.skills " +
            "LEFT JOIN FETCH js.applications")
    List<JobSeeker> findAllWithAssociations();

    // Find job seeker by email
    @Query("SELECT DISTINCT js FROM JobSeeker js LEFT JOIN FETCH js.experiences LEFT JOIN FETCH js.skills WHERE js.user.email = :email")
    Optional<JobSeeker> findByEmailWithAssociations(String email);
}