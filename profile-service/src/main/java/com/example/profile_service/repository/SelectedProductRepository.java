package com.example.profile_service.repository;



import com.example.profile_service.entity.SelectedProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectedProductRepository extends ReactiveMongoRepository<SelectedProduct, String> {}
