package com.example.product_service.configuration;


import com.example.product_service.dto.response.ApiResponse;
import com.example.product_service.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> commence(
            ServerWebExchange exchange, AuthenticationException authException) {
        return Mono.fromRunnable(() -> {
            ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(errorCode.getStatusCode().value()));
            exchange.getResponse().getHeaders().setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));

            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .code(errorCode.getCode())
                    .message(errorCode.getMessage())
                    .build();

            try {
                String responseBody = objectMapper.writeValueAsString(apiResponse);
                exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes())));
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.valueOf(errorCode.getStatusCode().value()));
            }
        });
    }
}
