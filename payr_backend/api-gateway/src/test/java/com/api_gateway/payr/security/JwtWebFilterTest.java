package com.api_gateway.payr.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import lombok.extern.slf4j.Slf4j;

import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JwtWebFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private WebFilterChain chain;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpHeaders headers;

    @InjectMocks
    private JwtWebFilter jwtWebFilter;

    @BeforeEach
    void setUp() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
    }

    @Test
    void filter_NoAuthHeader_ContinuesChain() {
        log.info("Testing filter: No Auth Header scenario");
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = jwtWebFilter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain).filter(exchange);
        verifyNoInteractions(jwtUtil);
        log.info("Finished testing filter: No Auth Header scenario");
    }

    @Test
    void filter_InvalidHeaderFormat_ContinuesChain() {
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidPrefix token");
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = jwtWebFilter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain).filter(exchange);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void filter_InvalidToken_ContinuesChain() {
        String token = "invalid-token";
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtUtil.isTokenValid(token)).thenReturn(false);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = jwtWebFilter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain).filter(exchange);
    }

    @Test
    void filter_ValidToken_SetsSecurityContext() {
        log.info("Testing filter: Valid Token scenario");
        String token = "valid-token";
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtUtil.isTokenValid(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn("test@example.com");
        when(jwtUtil.extractRole(token)).thenReturn("CUSTOMER");
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = jwtWebFilter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        verify(chain).filter(exchange);
        verify(jwtUtil).extractEmail(token);
        verify(jwtUtil).extractRole(token);
        log.info("Finished testing filter: Valid Token scenario");
    }
}
