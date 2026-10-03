package com.panafrican.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
    @EnableMethodSecurity
    public class SecurityConfiguration {

    @Bean
            SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
                        return http
                                            .csrf(csrf -> csrf.disable())
                                            .cors(cors -> cors.configurationSource(corsConfigurationSource))
                                            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                            .authorizeHttpRequests(auth -> auth
                                                                                           .requestMatchers("/actuator/health/**", "/api/v1/health").permitAll()
                                                                                           .requestMatchers(HttpMethod.GET, "/api/v1/regions/**", "/api/v1/countries/**").permitAll()
                                                                                           .requestMatchers(HttpMethod.POST, "/api/v1/applications", "/api/v1/messages").permitAll()
                                                                                           .anyRequest().authenticated())
                                            .httpBasic(Customizer.withDefaults())
                                            .build();
            }

    @Bean
            CorsConfigurationSource corsConfigurationSource(
                            @Value("${FRONTEND_ORIGINS:http://localhost:5173}") String frontendOrigins) {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Arrays.stream(frontendOrigins.split(","))
                                                                        .map(String::trim)
                                                                        .filter(origin -> !origin.isEmpty())
                                                                        .toList());
                        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "OPTIONS"));
                        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                        configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                        source.registerCorsConfiguration("/api/**", configuration);
                        return source;
            }

    @Bean
            PasswordEncoder passwordEncoder() {
                        return new BCryptPasswordEncoder();
            }
    }
