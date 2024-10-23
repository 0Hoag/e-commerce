package com.example.profile_service.repository;



import com.example.profile_service.entity.Orders;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends ReactiveMongoRepository<Orders, String> {}
