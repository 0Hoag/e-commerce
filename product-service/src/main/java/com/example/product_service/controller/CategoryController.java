package com.example.product_service.controller;

import com.example.product_service.dto.request.CategoryRequest;
import com.example.product_service.dto.request.CategoryUpdateRequest;
import com.example.product_service.dto.response.CategoryResponse;
import com.example.product_service.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping("/registration")
    public Mono<CategoryResponse> toCreateCategory(@RequestBody Mono<CategoryRequest> request) {
        return categoryService.toCreateCategory(request);
    }

    @GetMapping("/getCategory/{id}")
    public Mono<CategoryResponse> getCategory(@PathVariable String id) {
        return categoryService.getCategory(id);
    }

    @GetMapping("/getAllCategory")
    public Flux<CategoryResponse> getCategory() {
        return categoryService.getAllCategory();
    }

    @GetMapping("/getParentCategoryId/{id}")
    public Flux<CategoryResponse> getParentCategoryId(@PathVariable String id) {
        return categoryService.findByParentCategoryId(id);
    }

    @DeleteMapping("/deleteCategory/{id}")
    public Mono<Void> deleteCategory(@PathVariable String id) {
        return categoryService.deleteCategory(id);
    }

    @PutMapping("/updateCategory/{id}")
    public Mono<CategoryResponse> updateCategory(@PathVariable String id, @RequestBody CategoryUpdateRequest request) {
        return categoryService.updateCategory(id, request);
    }
}
