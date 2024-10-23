package com.example.profile_service.repository;

import com.example.profile_service.entity.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile, String> {
    Mono<Profile> findByUserId(String userId);
}
