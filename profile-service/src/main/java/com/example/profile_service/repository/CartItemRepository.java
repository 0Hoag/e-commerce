package com.example.profile_service.repository;




import com.example.profile_service.entity.CartItem;
import com.example.profile_service.entity.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CartItemRepository extends ReactiveMongoRepository<CartItem, String> {
    Flux<CartItem> findByProfile(Profile profile);
}
