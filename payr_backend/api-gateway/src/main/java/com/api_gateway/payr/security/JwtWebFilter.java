package com.api_gateway.payr.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//
//        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//        // If no token or wrong format, continue
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return chain.filter(exchange);
//        }
//
//        String token = authHeader.substring(7);
//
//        // If invalid token, continue
//        if (!jwtUtil.isTokenValid(token)) {
//            return chain.filter(exchange);
//        }
//
//        // Extract username or ID from token
//        String email = jwtUtil.extractEmail(token);
//        String role = jwtUtil.extractRole(token);
//
//        if (email != null) {
//            // Create reactive authentication
//            String authority = "ROLE_" + role;
//            System.out.printf(authority);
//            var authToken = new UsernamePasswordAuthenticationToken(
//                    email,
//                    null,
//                    List.of(new SimpleGrantedAuthority(authority)) // adjust roles if needed
//            );
//
//            // Set security context
//            return chain.filter(exchange)
//                    .contextWrite(ctx -> ctx.put(
//                            ServerSecurityContextRepository.class.getName(),
//                            new SecurityContextImpl(authToken)
//                    ));
//        }
//
//        return chain.filter(exchange);
//    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            return chain.filter(exchange);
        }

        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        if (email != null && role != null) {

            String authority = "ROLE_" + role;
            var authToken = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    List.of(new SimpleGrantedAuthority(authority))
            );

            // Set authenticated context correctly
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
        }

        return chain.filter(exchange);
    }
}