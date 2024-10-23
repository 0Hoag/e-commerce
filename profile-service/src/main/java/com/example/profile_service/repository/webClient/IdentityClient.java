package com.example.profile_service.repository.webClient;

import com.example.profile_service.dto.IntrospectResponse;
import com.example.profile_service.dto.identity.IntrospectRequest;
import com.example.profile_service.dto.identity.TokenExchangeParam;
import com.example.profile_service.dto.identity.TokenExchangeResponse;
import com.example.profile_service.dto.identity.UserCreationParam;
import com.example.profile_service.exception.AppException;
import com.example.profile_service.exception.ErrorCode;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class IdentityClient {
    WebClient webClient;

    @Value("${idp.url}")
    @NonFinal
    String identityClientsUrl;

    @Autowired
    public IdentityClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8180").build();
    }

    public Mono<IntrospectResponse> authorizationToken(IntrospectRequest param) {
        return webClient.post()
                .uri("/realms/devteria/protocol/openid-connect/token/introspect")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("token", param.getToken())
                        .with("client_id", param.getClient_id())
                        .with("client_secret", param.getClient_secret()))
                .retrieve()
                .bodyToMono(IntrospectResponse.class)
                .doOnNext(response ->
                        log.info("Introspect token successfully: {}", response))
                .doOnError(throwable ->
                        log.error("Introspect token error: {}", throwable.getMessage()));
    }

    public Mono<TokenExchangeResponse> exchangeToken(TokenExchangeParam param) {
        // Chuyển đổi param thành MultiValueMap để gửi dưới dạng form-urlencoded
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", param.getGrant_type());
        formData.add("client_id", param.getClient_id());
        formData.add("client_secret", param.getClient_secret());
        formData.add("scope", param.getScope());

        return webClient.post()
                .uri("/realms/devteria/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(TokenExchangeResponse.class)
                .doOnNext(response ->
                        log.info("Exchange token successfully: {}", response))
                .doOnError(throwable ->
                        log.error("Exchange token error: {}", throwable.getMessage()));
    }

    public Mono<String> createUser(String token, UserCreationParam param) {
        return webClient.post()
                .uri("/admin/realms/devteria/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .bodyValue(param)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        // Keycloak trả về userId trong header Location
                        String location = response.headers().asHttpHeaders().getFirst(HttpHeaders.LOCATION);
                        if (location != null) {
                            // Tách userId từ URL trả về trong header Location
                            String[] segments = location.split("/");
                            String userId = segments[segments.length - 1];
                            log.info("User created successfully with ID: {}", userId);
                            return Mono.just(userId);
                        } else {
                            log.error("User created but 'Location' header is missing.");
                            return Mono.error(new AppException(ErrorCode.USER_NOT_EXISTED));
                        }
                    } else {
                        log.error("Failed to create user. Status: {}", response.statusCode());
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .doOnError(throwable -> log.error("User creation error: {}", throwable.getMessage()));
    }
}
