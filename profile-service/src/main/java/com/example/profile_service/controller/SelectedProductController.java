package com.example.profile_service.controller;

import com.example.profile_service.dto.selected.request.AddSelectedProductRequest;
import com.example.profile_service.dto.selected.request.SelectedProductRequest;
import com.example.profile_service.dto.selected.response.SelectedProductResponse;
import com.example.profile_service.service.SelectedProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/selectedProduct")
public class SelectedProductController {

    SelectedProductService selectedProductService;

    @PostMapping("/registration")
    public Mono<SelectedProductResponse> createSelectedProduct(
            @RequestBody Mono<SelectedProductRequest> request,
            @RequestHeader("Authorization") String token
            ) {
        return selectedProductService.toCreate(request, token);
    }

    @PostMapping("/addSelectedProductWithProfile/{orderId}")
    public Mono<Void> addSelectedProductWithUser(
            @PathVariable String orderId,
            @RequestBody AddSelectedProductRequest request,
            @RequestHeader("Authorization") String token
        ) {
        return selectedProductService.addSelectedByOrdersResponse(orderId, request);
    }

    @GetMapping("/getAllSelectedProduct")
    public Flux<SelectedProductResponse> getAllProduct(@RequestHeader("Authorization") String token) {
        return selectedProductService.loadAllSelectedProduct(token);
    }
}
