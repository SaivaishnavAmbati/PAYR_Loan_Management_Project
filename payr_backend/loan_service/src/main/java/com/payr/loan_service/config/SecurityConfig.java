package com.payr.loan_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/loans/loanTypes/getLoans",
                                "/api/loans/loanTypes/getLoanById/**"
                        ).permitAll()
                        .requestMatchers("/api/loans/loanTypes/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/loans/loanTypes/loan/admin/validate").hasRole("ADMIN")
                        .requestMatchers("/api/loans/loanApplication/apply").hasRole("CUSTOMER")
                        .requestMatchers("/api/loans/loanApplication/loan/apply/validate").hasRole("CUSTOMER")
                        .requestMatchers("/api/loans/loanOfficer/**").hasRole("OFFICER")
                        .requestMatchers("/api/loans/**").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
