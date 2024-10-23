package com.example.product_service.service;

import com.example.product_service.dto.request.CategoryRequest;
import com.example.product_service.dto.request.CategoryUpdateRequest;
import com.example.product_service.dto.response.CategoryResponse;
import com.example.product_service.entity.Category;
import com.example.product_service.exception.AppException;
import com.example.product_service.exception.ErrorCode;
import com.example.product_service.mapper.CategoryMapper;
import com.example.product_service.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryMapper categoryMapper;
    CategoryRepository categoryRepository;

    public Mono<CategoryResponse> toCreateCategory(Mono<CategoryRequest> request) {
        return request
                .map(categoryMapper::toCategory)
                .flatMap(categoryRepository::save)
                .map(categoryMapper::toCategoryResponse);
    }

    public Mono<CategoryResponse> getCategory(String id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.CATEGORY_NOT_EXISTED)))
                .map(categoryMapper::toCategoryResponse);
    }

    public Flux<CategoryResponse> getAllCategory() {
        return categoryRepository.findAll()
                .map(categoryMapper::toCategoryResponse);
    }

    public Flux<CategoryResponse> findByParentCategoryId(String parentCategoryId) {
        return categoryRepository.findByParentCategoryId(parentCategoryId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.CATEGORY_DAD_NOT_EXISTED)))
                .map(categoryMapper::toCategoryResponse);
    }

    public Mono<Void> deleteCategory(String id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.CATEGORY_DAD_NOT_EXISTED)))
                .flatMap(category -> categoryRepository.findByParentCategoryId(id)
                            .flatMap(childCategory -> categoryRepository.deleteById(childCategory.getId()))
                            .then(categoryRepository.deleteById(category.getId()))
                );
    }

    public Mono<CategoryResponse> updateCategory(String id, CategoryUpdateRequest request) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.CATEGORY_NOT_EXISTED)))
                .map(category -> {
                    categoryMapper.updateCategory(category, request);
                    return categoryMapper.toCategoryResponse(category);
                });

    }
}
