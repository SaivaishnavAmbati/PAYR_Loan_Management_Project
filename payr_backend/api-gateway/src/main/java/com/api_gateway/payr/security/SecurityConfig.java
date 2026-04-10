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
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .pathMatchers("/v3/api-docs/**").permitAll()
                        .pathMatchers("/auth-service/v3/api-docs/**").permitAll()
                        .pathMatchers("/loan-service/v3/api-docs/**").permitAll()
                        .pathMatchers("/user-service/v3/api-docs/**").permitAll()
                        .pathMatchers("/document-service/v3/api-docs/**").permitAll()
                        .pathMatchers("/notification-service/v3/api-docs/**").permitAll()
                        .pathMatchers("/payment-service/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/auth-service/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/loan-service/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/user-service/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/documents/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/notifications/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/payments/v3/api-docs/**").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/webjars/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/favicon.ico").permitAll()

                        // Loan service (aligned with loan-service SecurityConfig)
                        .pathMatchers(HttpMethod.GET, "/api/loans/loanTypes/getLoans").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/loans/loanTypes/getLoanById/**").permitAll()
                        .pathMatchers("/api/loans/loanTypes/admin/**").hasRole(Role.ADMIN.name())
                        .pathMatchers("/api/loans/loanTypes/loan/admin/validate").hasRole(Role.ADMIN.name())
                        .pathMatchers(HttpMethod.POST, "/api/loans/loanApplication/apply").hasRole(Role.CUSTOMER.name())
                        .pathMatchers(HttpMethod.POST, "/api/loans/loanApplication/loan/apply/validate")
                        .hasRole(Role.CUSTOMER.name())
                        .pathMatchers("/api/loans/loanOfficer/**").hasRole(Role.OFFICER.name())
                        .pathMatchers("/api/loans/**").authenticated()

                        // User service: auth registration callback (not JWT) must bypass admin rule
                        .pathMatchers(HttpMethod.POST, "/api/users/from-auth").permitAll()
                        .pathMatchers("/api/users/**").hasRole(Role.ADMIN.name())

                        .pathMatchers("/api/documents/**").authenticated()
                        .pathMatchers("/notifications/**").authenticated()
                        .pathMatchers("/api/payments/**").authenticated()

                        .anyExchange().authenticated()
                )
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
