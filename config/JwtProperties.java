package com.dauphine.jobportal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jobportal.app")
@Data
public class JwtProperties {
    private String jwtSecret;
    private int jwtExpirationMs;
}