package com.api_gateway.payr.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtWebFilter jwtWebFilter;

    public SecurityConfig(JwtWebFilter jwtWebFilter) {
        this.jwtWebFilter = jwtWebFilter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeExchange(exchanges -> exchanges

                                // allow unauthenticated access for these endpoints
                                .pathMatchers("/auth/**").permitAll()
                                .pathMatchers("/actuator/**").permitAll()
//                                .pathMatchers("/swagger-ui/**").permitAll()
                                .pathMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                                .pathMatchers("/v3/api-docs/**").permitAll()      // OpenAPI spec
                                .pathMatchers("/auth-service/v3/api-docs/**").permitAll()
                                .pathMatchers("/loan-service/v3/api-docs/**").permitAll()
                                .pathMatchers("/user-service/v3/api-docs/**").permitAll()
                                .pathMatchers("/document-service/v3/api-docs/**").permitAll()

//                        -------------
                                // Add these lines to permit microservice API docs via gateway
                                .pathMatchers("/api/auth-service/v3/api-docs/**").permitAll()
                                .pathMatchers("/api/loan-service/v3/api-docs/**").permitAll()
                                .pathMatchers("/api/user-service/v3/api-docs/**").permitAll()
                                .pathMatchers("/api/documents/v3/api-docs/**").permitAll()
                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                .pathMatchers("/webjars/**").permitAll()          // static JS/CSS assets
                                .pathMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                                // seller routes
                                .pathMatchers(HttpMethod.POST, "/api/loanOfficer/applications/**").hasRole(Role.OFFICER.name())
                                .pathMatchers(HttpMethod.GET, "/api/loanApplication/**").permitAll()
                                .pathMatchers("/api/loanApplication/**").hasRole(Role.CUSTOMER.name())
                                .pathMatchers("/api/users/**").hasRole(Role.ADMIN.name())

                                // everything else needs authentication
                                .anyExchange().authenticated()
                )

                // Add JWT filter before authentication
                .addFilterAt(jwtWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec
                                .authenticationEntryPoint((exchange, ex) ->
                                        Mono.fromRunnable(() ->
                                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)
                                        )
                                ))
                .build();

    }
}