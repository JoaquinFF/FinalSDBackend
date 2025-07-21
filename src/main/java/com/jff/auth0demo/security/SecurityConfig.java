package com.jff.auth0demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos - no requieren autenticación
                        .requestMatchers("/public", "/public/**").permitAll()
                        // Endpoints de admin - requieren rol ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Endpoints privados - requieren rol CLIENTE o ADMIN
                        .requestMatchers("/private/**").hasAnyRole("CLIENTE", "ADMIN")
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            
            // Buscar roles en el claim específico de Auth0
            String rolesClaimName = "https://hola/roles";
            Object rolesClaim = jwt.getClaim(rolesClaimName);
            
            if (rolesClaim != null) {
                if (rolesClaim instanceof List) {
                    List<?> roles = (List<?>) rolesClaim;
                    for (Object role : roles) {
                        String roleString = role.toString().toUpperCase();
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleString));
                    }
                } else if (rolesClaim instanceof String) {
                    String roleString = rolesClaim.toString().toUpperCase();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + roleString));
                }
            }
            
            return authorities;
        });
        
        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes específicos
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:3000",     // React default
            "http://localhost:3001",     // React alternate
            "http://localhost:8080",     // Spring Boot default
            "http://localhost:4200",     // Angular default
            "http://localhost:5173",     // Vite default
            "http://127.0.0.1:3000",    // IP local
            "http://127.0.0.1:3001",
            "http://127.0.0.1:8080",
            "http://127.0.0.1:4200",
            "http://127.0.0.1:5173"
        ));
        
        // Permitir métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Permitir headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept", 
            "Origin", 
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Permitir credenciales (cookies, headers de autorización)
        configuration.setAllowCredentials(true);
        
        // Exponer headers específicos
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Configurar el tiempo máximo de cache para preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
