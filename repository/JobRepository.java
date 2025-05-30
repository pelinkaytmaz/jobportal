package com.dauphine.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dauphine.jobportal.model.Job;
import com.dauphine.jobportal.model.enums.ExperienceLevel;
import com.dauphine.jobportal.model.enums.JobType;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

       // Find jobs by title containing keyword
       List<Job> findByTitleContainingIgnoreCase(String keyword);

       // Find jobs by location
       List<Job> findByLocationContainingIgnoreCase(String location);

       // Find jobs by type
       List<Job> findByType(JobType type);

       // Find jobs by experience level
       List<Job> findByExperienceLevel(ExperienceLevel experienceLevel);

       // Find jobs by salary range
       List<Job> findBySalaryMinGreaterThanEqualAndSalaryMaxLessThanEqual(Double minSalary, Double maxSalary);

       // Find jobs with advanced filtering
       @Query(value = """
                         SELECT * FROM jobs WHERE
                             (:keyword IS NULL OR title ILIKE CONCAT('%', :keyword, '%')) AND
                             (:location IS NULL OR location ILIKE CONCAT('%', :location, '%')) AND
                             (:type IS NULL OR type = :type) AND
                             (:experienceLevel IS NULL OR experience_level = :experienceLevel) AND
                             (:minSalary IS NULL OR salary_min >= :minSalary) AND
                             (:maxSalary IS NULL OR salary_max <= :maxSalary)
                         ORDER BY posting_date DESC
                     """, nativeQuery = true)
       List<Job> findJobsWithFilters(
                     @Param("keyword") String keyword,
                     @Param("location") String location,
                     @Param("type") String type,
                     @Param("experienceLevel") String experienceLevel,
                     @Param("minSalary") Double minSalary,
                     @Param("maxSalary") Double maxSalary);

}