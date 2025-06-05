package com.dauphine.jobportal.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dauphine.jobportal.dto.ApplicationCreateDTO;
import com.dauphine.jobportal.dto.ApplicationDTO;
import com.dauphine.jobportal.dto.CompanyCreateDTO;
import com.dauphine.jobportal.dto.CompanyDTO;
import com.dauphine.jobportal.dto.ExperienceCreateDTO;
import com.dauphine.jobportal.dto.ExperienceDTO;
import com.dauphine.jobportal.dto.JobCreateDTO;
import com.dauphine.jobportal.dto.JobDTO;
import com.dauphine.jobportal.dto.JobSeekerCreateDTO;
import com.dauphine.jobportal.dto.JobSeekerDTO;
import com.dauphine.jobportal.dto.SkillDTO;
import com.dauphine.jobportal.model.Application;
import com.dauphine.jobportal.model.Company;
import com.dauphine.jobportal.model.Experience;
import com.dauphine.jobportal.model.Job;
import com.dauphine.jobportal.model.JobSeeker;
import com.dauphine.jobportal.model.Skill;
import com.dauphine.jobportal.model.User;

@Component
public class EntityDTOMapper {

    // Job mappings
    public JobDTO toJobDTO(Job job) {
        if (job == null) {
            return null;
        }

        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setResponsibilities(job.getResponsibilities());
        dto.setQualifications(job.getQualifications());
        dto.setLocation(job.getLocation());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setType(job.getType());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setPostingDate(job.getPostingDate());

        if (job.getCompany() != null) {
            dto.setCompany(toCompanyDTO(job.getCompany()));
        }

        return dto;
    }

    public Job toJobEntity(JobCreateDTO dto, Company company) {
        if (dto == null) {
            return null;
        }

        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setResponsibilities(dto.getResponsibilities());
        job.setQualifications(dto.getQualifications());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setType(dto.getType());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setCompany(company);

        return job;
    }

    // Company mappings
    public CompanyDTO toCompanyDTO(Company company) {
        if (company == null) {
            return null;
        }

        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setLocation(company.getLocation());
        dto.setWebsite(company.getWebsite());

        return dto;
    }

    public Company toCompanyEntity(CompanyCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Company company = new Company();
        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setLocation(dto.getLocation());
        company.setWebsite(dto.getWebsite());

        return company;
    }

    // JobSeeker mappings
    public JobSeekerDTO toJobSeekerDTO(JobSeeker jobSeeker) {
        if (jobSeeker == null) {
            return null;
        }

        JobSeekerDTO dto = new JobSeekerDTO();
        dto.setUser(jobSeeker.getUser());
        dto.setId(jobSeeker.getId());
        dto.setFirstName(jobSeeker.getFirstName());
        dto.setLastName(jobSeeker.getLastName());
        dto.setPhoneNumber(jobSeeker.getPhoneNumber());

        if (jobSeeker.getExperiences() != null) {
            Set<ExperienceDTO> experienceDTOs = jobSeeker.getExperiences().stream()
                    .map(this::toExperienceDTO)
                    .collect(Collectors.toSet());
            dto.setExperiences(experienceDTOs);
        }

        if (jobSeeker.getSkills() != null) {
            Set<SkillDTO> skillDTOs = jobSeeker.getSkills().stream()
                    .map(this::toSkillDTO)
                    .collect(Collectors.toSet());
            dto.setSkills(skillDTOs);
        }

        return dto;
    }

    public JobSeeker toJobSeekerEntity(JobSeekerCreateDTO dto, User user) {
        if (dto == null) {
            return null;
        }

        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setFirstName(dto.getFirstName());
        jobSeeker.setLastName(dto.getLastName());
        jobSeeker.setPhoneNumber(dto.getPhoneNumber());
        jobSeeker.setUser(user);

        if (dto.getExperiences() != null) {
            Set<Experience> experiences = dto.getExperiences().stream()
                    .map(expDto -> {
                        Experience exp = toExperienceEntity(expDto);
                        exp.setJobSeeker(jobSeeker);
                        return exp;
                    })
                    .collect(Collectors.toSet());
            jobSeeker.setExperiences(experiences);
        }

        return jobSeeker;
    }

    // Experience mappings
    public ExperienceDTO toExperienceDTO(Experience experience) {
        if (experience == null) {
            return null;
        }

        ExperienceDTO dto = new ExperienceDTO();
        dto.setId(experience.getId());
        dto.setTitle(experience.getTitle());
        dto.setDescription(experience.getDescription());
        dto.setStartDate(experience.getStartDate());
        dto.setEndDate(experience.getEndDate());

        return dto;
    }

    public Experience toExperienceEntity(ExperienceCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Experience experience = new Experience();
        experience.setTitle(dto.getTitle());
        experience.setDescription(dto.getDescription());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());

        return experience;
    }

    // Skill mappings
    public SkillDTO toSkillDTO(Skill skill) {
        if (skill == null) {
            return null;
        }

        SkillDTO dto = new SkillDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());

        return dto;
    }

    public Set<Skill> toSkillEntities(Set<String> skillNames) {
        if (skillNames == null) {
            return new HashSet<>();
        }

        return skillNames.stream()
                .map(name -> {
                    Skill skill = new Skill();
                    skill.setName(name);
                    return skill;
                })
                .collect(Collectors.toSet());
    }

    // Application mappings
    public ApplicationDTO toApplicationDTO(Application application) {
        if (application == null) {
            return null;
        }

        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(application.getId());
        dto.setApplicationDate(application.getApplicationDate());
        dto.setStatus(application.getStatus());

        if (application.getJob() != null) {
            dto.setJob(toJobDTO(application.getJob()));
        }

        if (application.getJobSeeker() != null) {
            dto.setJobSeeker(toJobSeekerDTO(application.getJobSeeker()));
        }

        return dto;
    }

    public Application toApplicationEntity(ApplicationCreateDTO dto, Job job, JobSeeker jobSeeker) {
        if (dto == null) {
            return null;
        }

        Application application = new Application();
        application.setJob(job);
        application.setJobSeeker(jobSeeker);
        application.setStatus(dto.getStatus());

        return application;
    }
}