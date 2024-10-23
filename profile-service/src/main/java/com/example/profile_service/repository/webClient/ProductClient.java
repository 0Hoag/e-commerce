package com.example.profile_service.repository.webClient;

import com.example.profile_service.dto.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ProductClient {
    @Value("${app.services.product}")
    String productService;

    WebClient webClient;

    @Autowired
    public ProductClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081/product").build();
    }

    public Mono<ProductResponse> fetchProductResponse(String id, String token) {
        return webClient.get()
                .uri("/" + id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }
}
