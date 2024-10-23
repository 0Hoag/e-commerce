package com.example.product_service.controller;

import com.example.product_service.dto.request.*;
import com.example.product_service.dto.response.ApiResponse;
import com.example.product_service.dto.response.AuthorResponse;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.Author;
import com.example.product_service.service.AuthorService;
import com.example.product_service.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/author")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorController {
    AuthorService authorService;

    @PostMapping("/registration")
    Mono<AuthorResponse> createAuthor(@RequestBody Mono<CreateAuthorRequest> request) {
        return authorService.createAuthor(request);
    }

    @PostMapping("/addProductToAuthor/{id}")
    Mono<Author> addProductToAuthor(@PathVariable String id, @RequestBody AddToProductRequest request) {
        return authorService.addProductToAuthor(id, request);
    }

    @DeleteMapping("/removeProductToAuthor/{id}")
    Mono<Author> removeProductToAuthor(@PathVariable String id, @RequestBody RemoveToProductRequest request) {
        return authorService.removeProductToAuthor(id, request);
    }

    @GetMapping("/{id}")
    Mono<AuthorResponse> getProduct(@PathVariable String id) {
        return authorService.getAuthor(id);
    }

    @DeleteMapping("/{id}")
    Mono<Void> deleteProduct(@PathVariable String id) {
        return authorService.deleteAuthor(id);
    }

    @PutMapping("/{id}")
    Mono<AuthorResponse> updateProduct(@PathVariable String id, @RequestBody UpdateAuthorRequest request) {
        return authorService.updateAuthor(id, request);
    }
}
