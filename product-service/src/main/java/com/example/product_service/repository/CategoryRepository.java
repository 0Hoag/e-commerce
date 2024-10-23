package com.example.product_service.repository;

import com.example.product_service.entity.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
    Flux<Category> findByParentCategoryId(String parentCategoryId);
}
