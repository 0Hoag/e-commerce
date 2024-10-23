package com.example.product_service.repository;

import com.example.product_service.dto.request.UpdateAuthorRequest;
import com.example.product_service.dto.response.AuthorResponse;
import com.example.product_service.entity.Author;
import org.mapstruct.MappingTarget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {
}
