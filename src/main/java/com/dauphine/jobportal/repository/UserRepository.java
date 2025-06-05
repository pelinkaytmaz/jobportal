package com.dauphine.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dauphine.jobportal.model.Company;
import com.dauphine.jobportal.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Méthodes de base pour l'authentification
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // CORRECTION : Utiliser la navigation d'objet pour accéder à l'ID de l'entreprise
    List<User> findByCompany_Id(Long companyId);
    
    // Alternative : Utiliser l'objet Company directement
    List<User> findByCompany(Company company);

    // Recherche par nom d'utilisateur ou email (pour la fonction search)
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);

    // Requêtes personnalisées avec @Query (alternatives plus explicites)
    @Query("SELECT u FROM User u WHERE u.company.id = :companyId")
    List<User> findUsersByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT u FROM User u WHERE u.company = :company")
    List<User> findUsersByCompany(@Param("company") Company company);

    // Requêtes utiles supplémentaires
    @Query("SELECT u FROM User u WHERE u.company IS NULL")
    List<User> findUsersWithoutCompany();

    @Query("SELECT u FROM User u WHERE u.company IS NOT NULL")
    List<User> findUsersWithCompany();

    @Query("SELECT COUNT(u) FROM User u WHERE u.company.id = :companyId")
    long countUsersByCompanyId(@Param("companyId") Long companyId);

    // Recherche avancée avec jointures
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.company LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithCompanyAndRoles(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.company LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithCompanyAndRoles(@Param("username") String username);
}