package com.example.profile_service.controller;

import com.example.profile_service.dto.IntrospectResponse;
import com.example.profile_service.dto.request.ProfileUpdateRequest;
import com.example.profile_service.dto.request.RegistrationRequest;
import com.example.profile_service.dto.response.ProfileResponse;
import com.example.profile_service.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;

    @PostMapping("/registration")
    public Mono<ProfileResponse> toCreateProfile(@RequestBody RegistrationRequest request) {
        return profileService.createProfile(request);
    }

    @GetMapping("/my-profile")
    public Mono<ProfileResponse> getMyInfo(
    ) {
        return profileService.getMyInfo();
    }

    @PostMapping("/auth/introspect")
    public Mono<IntrospectResponse> introspectToken(@RequestParam String token) {
        return profileService.authorizationToken(token);
    }

    @PutMapping("/updateProfile/{profileId}")
    public Mono<ProfileResponse> updateProfile(@PathVariable String profileId, @RequestBody Mono<ProfileUpdateRequest> request) {
        return profileService.updateProfile(profileId, request);
    }

    @GetMapping("/getAllProfile")
    public Flux<ProfileResponse> loadAllProfile() {
        return profileService.loadAllProfile();
    }

    @GetMapping("/{profileId}")
    public Mono<ProfileResponse> loadProfile(@PathVariable String profileId) {
        return profileService.getProfile(profileId);
    }

    @DeleteMapping("/deleteProfile/{profileId}")
    public Mono<Void> deleteProfile(@PathVariable String profileId) {
        return profileService.deleteProfile(profileId);
    }
}
