package com.example.product_service.controller;

import com.example.product_service.dto.request.AddImageToProductRequest;
import com.example.product_service.dto.request.CreateProductRequest;
import com.example.product_service.dto.request.ProductUpdateRequest;
import com.example.product_service.dto.request.UpdateStockQuantityProductRequest;
import com.example.product_service.dto.response.ApiResponse;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.DataInput;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;
    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Mono<ProductResponse> createProduct(
            @RequestPart("request") String requestJson,
            @RequestPart("file") FilePart filePart) {
        ObjectMapper objectMapper = new ObjectMapper();
        Mono<CreateProductRequest> request = Mono.fromCallable(() ->
                objectMapper.readValue(requestJson, CreateProductRequest.class))
                .subscribeOn(Schedulers.boundedElastic());

        return productService.createProduct(request, filePart);
    }

    @PutMapping("/updateImageToProduct/{postId}")
    Mono<ProductResponse> updateImageToProduct(@PathVariable String postId, @RequestBody AddImageToProductRequest request) {
        return productService.updateImageToProduct(postId, request);
    }

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Mono<Map> uploadPhoto(@RequestPart("file") FilePart filePart) {
        return productService.uploadPhoto(filePart);
    }

    @GetMapping("/{id}")
    Mono<ProductResponse> getProduct(@PathVariable String id) {
        return productService.getProduct(id);
    }

    @GetMapping("/getAllProduct")
    Flux<ProductResponse> getAllProduct() {
        return productService.getAllProduct();
    }

    @DeleteMapping("/{id}")
    Mono<Void> deleteProduct(@PathVariable String id) {
       return productService.deleteProduct(id);
    }

    @PutMapping("/{id}")
    Mono<ProductResponse> updateQuantityProduct(@PathVariable String id, @RequestBody UpdateStockQuantityProductRequest request) {
        return productService.updateQuantityProduct(id, request);
    }

    @PutMapping("/updateProduct/{id}")
    Mono<ProductResponse> updateProduct(@PathVariable String id, @RequestBody ProductUpdateRequest request) {
        return productService.updateProduct(id, request);
    }
}
