package com.example.config;

import com.example.auth.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String PREFIX = "/api/v1";
    private final JwtRequestFilter jwtRequestFilter;
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/hello", "/secure", "/authenticate").permitAll() // Public endpoints
                        //.requestMatchers( "/password", "/api/v1/signup/", "/error").permitAll() // Public endpoints
                        .requestMatchers(HttpMethod.POST, PREFIX + "/likes").hasAnyRole("USER")
                        .requestMatchers(PREFIX + "/comments", PREFIX + "/comments/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.POST, PREFIX + "/questions").hasAnyRole("QUESTIONER")
                        .requestMatchers(HttpMethod.GET, PREFIX + "/questions/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, PREFIX + "/questions/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.PUT, PREFIX + "/questions/**").hasAnyRole("QUESTIONER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, PREFIX + "/exams").hasAnyRole("EXAMINER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, PREFIX + "/exams").permitAll()
                        .requestMatchers(HttpMethod.PUT, PREFIX + "/exams/**").hasAnyRole("EXAMINER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, PREFIX + "/exams/*/questions").hasAnyRole("EXAMINER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, PREFIX + "/exams/*/questions").permitAll()
                        .requestMatchers(HttpMethod.PUT, PREFIX + "/exams/*/questions/**").hasAnyRole("EXAMINER", "ADMIN")

                        .anyRequest().authenticated()  // Secure other endpoints
                )
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter
        return http.build();
    }
}
