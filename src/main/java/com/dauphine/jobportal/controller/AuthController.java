package com.dauphine.jobportal.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dauphine.jobportal.dto.JobSeekerCreateDTO;
import com.dauphine.jobportal.dto.JwtResponseDTO;
import com.dauphine.jobportal.dto.LoginRequestDTO;
import com.dauphine.jobportal.dto.SignupRequestDTO;
import com.dauphine.jobportal.model.Company;
import com.dauphine.jobportal.model.JobSeeker;
import com.dauphine.jobportal.model.Role;
import com.dauphine.jobportal.model.Skill;
import com.dauphine.jobportal.model.User;
import com.dauphine.jobportal.model.enums.ERole;
import com.dauphine.jobportal.repository.CompanyRepository;
import com.dauphine.jobportal.repository.JobSeekerRepository;
import com.dauphine.jobportal.repository.RoleRepository;
import com.dauphine.jobportal.repository.UserRepository;
import com.dauphine.jobportal.security.JwtUtils;
import com.dauphine.jobportal.service.SkillService;
import com.dauphine.jobportal.util.EntityDTOMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentification", description = "API d'authentification et de gestion des utilisateurs")
public class AuthController {

        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        UserRepository userRepository;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        CompanyRepository companyRepository;

        @Autowired
        JobSeekerRepository jobSeekerRepository;

        @Autowired
        SkillService skillService;

        @Autowired
        PasswordEncoder encoder;

        @Autowired
        JwtUtils jwtUtils;

        @Autowired
        EntityDTOMapper entityMapper;

        @PostMapping("/signup")
        @Transactional
        @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Permet de créer un nouveau compte utilisateur avec un nom d'utilisateur, email et mot de passe uniques. "
                        + "L'utilisateur peut optionnellement être associé à une entreprise existante lors de l'inscription. "
                        + "Si aucun rôle n'est spécifié, l'utilisateur recevra le rôle ROLE_CANDIDAT par défaut.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "User registered successfully!"))),
                        @ApiResponse(responseCode = "400", description = "Erreur lors de la création", content = @Content(mediaType = "text/plain", examples = {
                                        @ExampleObject(name = "Username taken", value = "Error: Username is already taken!"),
                                        @ExampleObject(name = "Email in use", value = "Error: Email is already in use!"),
                                        @ExampleObject(name = "Company not found", value = "Error: Company with ID X is not found!"),
                                        @ExampleObject(name = "Missing JobSeeker data", value = "Error: Missing jobSeeker data with role jobseeker selected!")
                        })),
                        @ApiResponse(responseCode = "500", description = "Erreur interne - rôle non trouvé", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Error: Role ROLE_CANDIDAT is not found.")))
        })
        public String registerUser(
                        @Parameter(description = "Informations d'inscription de l'utilisateur", required = true, schema = @Schema(implementation = SignupRequestDTO.class)) @Valid @RequestBody SignupRequestDTO signUpRequest) {

                if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                        return "Error: Username is already taken!";
                }

                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                        return "Error: Email is already in use!";
                }

                // Vérifier si une entreprise est spécifiée et existe
                Company company = null;
                if (signUpRequest.getCompanyId() != null) {
                        company = companyRepository.findById(signUpRequest.getCompanyId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Error: Company with ID " + signUpRequest.getCompanyId() + " is not found!"));
                }

                // Créer l'utilisateur avec ou sans entreprise
                User user = new User(
                                signUpRequest.getUsername(),
                                signUpRequest.getEmail(),
                                encoder.encode(signUpRequest.getPassword()));
                
                // Associer l'entreprise si elle est spécifiée
                if (company != null) {
                        user.setCompany(company);
                }

                Set<Role> roles = new HashSet<>();

                if (signUpRequest.getRoles() == null || signUpRequest.getRoles().isEmpty()) {
                        Role userRole = roleRepository.findByName(ERole.ROLE_CANDIDAT)
                                        .orElseThrow(() -> new RuntimeException("Error: Role ROLE_CANDIDAT is not found."));
                        roles.add(userRole);
                } else {
                        signUpRequest.getRoles().forEach(roleReq -> {
                                ERole roleEnum = ERole.valueOf(roleReq.getName());
                                Role role = roleRepository.findByName(roleEnum)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Error: Role " + roleReq.getName() + " is not found."));
                                roles.add(role);
                        });
                }

                user.setRoles(roles);
                User userSaved = userRepository.save(user);

                // Créer le profil JobSeeker si le rôle CANDIDAT est présent
                if (roles.stream().anyMatch(r -> r.getName() == ERole.ROLE_CANDIDAT)) {
                        JobSeekerCreateDTO js = signUpRequest.getJobSeeker();

                        if (js == null) {
                                throw new IllegalArgumentException(
                                                "Error: Missing jobSeeker data with role jobseeker selected!");
                        }

                        JobSeeker jobSeeker = entityMapper.toJobSeekerEntity(js, userSaved);

                        // Process skills from DTO and add to JobSeeker entity
                        if (js.getSkills() != null && !js.getSkills().isEmpty()) {
                                Set<Skill> skills = js.getSkills().stream()
                                                .map(skillDTO -> skillService.findByNameOrCreate(skillDTO))
                                                .collect(Collectors.toSet());
                                jobSeeker.setSkills(skills);
                        }

                        System.out.println(jobSeeker.getUser().getId());

                        jobSeekerRepository.save(jobSeeker);
                }

                return "User registered successfully!";
        }

        @PostMapping("/login")
        @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur avec ses identifiants et retourne un token JWT pour les requêtes ultérieures. "
                        +
                        "Le token doit être inclus dans l'en-tête Authorization des requêtes suivantes. "
                        + "Les informations de l'entreprise associée à l'utilisateur sont incluses dans la réponse si disponibles.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Authentification réussie", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponseDTO.class), examples = @ExampleObject(value = """
                                        {
                                          "token": "eyJhbGciOiJIUzUxMiJ9...",
                                          "username": "john_doe",
                                          "roles": ["ROLE_CANDIDAT"],
                                          "companyId": 1,
                                          "companyName": "Tech Solutions Inc"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "error": "Unauthorized",
                                          "message": "Invalid credentials"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "error": "User not found",
                                          "message": "Username not found"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "error": "Internal Server Error",
                                          "message": "Authentication service unavailable"
                                        }
                                        """)))
        })
        public JwtResponseDTO authenticateUser(
                        @Parameter(description = "Identifiants de connexion (nom d'utilisateur et mot de passe)", required = true, schema = @Schema(implementation = LoginRequestDTO.class)) @Valid @RequestBody LoginRequestDTO loginRequest) {

                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                        loginRequest.getPassword()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        String jwt = jwtUtils.generateJwtToken(authentication);

                        var userDetails = (org.springframework.security.core.userdetails.User) authentication
                                        .getPrincipal();

                        Set<String> roles = userDetails.getAuthorities().stream()
                                        .map(item -> item.getAuthority())
                                        .collect(Collectors.toSet());

                        // Récupérer les informations complètes de l'utilisateur pour obtenir les données de l'entreprise
                        User user = userRepository.findByUsername(userDetails.getUsername())
                                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userDetails.getUsername()));

                        // Créer la réponse JWT avec les informations de l'entreprise si disponibles
                        JwtResponseDTO response = new JwtResponseDTO(jwt, userDetails.getUsername(), roles);
                        response.setId(user.getId());
                        if (user.hasCompany()) {
                                response.setCompanyId(user.getCompanyId());
                                response.setCompanyName(user.getCompanyName());
                        }

                        return response;

                } catch (BadCredentialsException e) {
                        System.out.println("Authentication failed: bad credentials");
                        throw e;
                } catch (UsernameNotFoundException e) {
                        System.out.println("Authentication failed: user not found");
                        throw e;
                } catch (Exception e) {
                        System.out.println("Authentication failed: " + e.getMessage());
                        throw e;
                }
        }
}