package com.example.product_service.repository;

import com.example.product_service.entity.Author;
import com.example.product_service.entity.Product;
import org.mapstruct.MappingTarget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
