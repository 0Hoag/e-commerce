package com.example.profile_service.service;

import com.example.profile_service.dto.IntrospectResponse;
import com.example.profile_service.dto.ProductResponse;
import com.example.profile_service.dto.identity.*;
import com.example.profile_service.dto.request.ProfileUpdateRequest;
import com.example.profile_service.dto.request.RegistrationRequest;
import com.example.profile_service.dto.response.ProfileResponse;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.exception.AppException;
import com.example.profile_service.exception.ErrorCode;
import com.example.profile_service.mapper.ProfileMapper;
import com.example.profile_service.repository.ProfileRepository;
import com.example.profile_service.repository.webClient.IdentityClient;
import com.example.profile_service.repository.webClient.ProductClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    IdentityClient identityClient;
    WebClient webClient;
    ProductClient productClient;


    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;

    @NonFinal
    @Value("${app.services.product}")
    String productService;

    public Mono<ProfileResponse> createProfile(RegistrationRequest request) {
        return exchangeToken()
                .flatMap(token -> createUser(token, request))
                .flatMap(userId -> saveProfile(userId, request))
                .doOnSubscribe(__ -> log.info("Starting profile creation process"))
                .doOnSuccess(__ -> log.info("Profile creation completed successfully"))
                .doOnError(e -> log.error("Error in profile creation: {}", e.getMessage()));
    }

    public Mono<ProfileResponse> getMyInfo() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .doOnNext(userId -> log.info("userId: {}", userId))
                .flatMap(userId -> profileRepository.findByUserId(userId)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_HAVE_PROFILE))))
                .map(profileMapper::toProfileResponse)
                .doOnSubscribe(__ -> log.info("Starting profile creation process"))
                .doOnSuccess(__ -> log.info("Profile creation completed successfully"))
                .doOnError(e -> log.error("Error in profile creation: {}", e.getMessage()));
    }

    public Mono<TokenExchangeResponse> exchangeToken() {
        return identityClient.exchangeToken(
                TokenExchangeParam.builder()
                        .grant_type("client_credentials")
                        .client_id(clientId)
                        .client_secret(clientSecret)
                        .scope("openid")
                        .build());
    }

    public Mono<IntrospectResponse> authorizationToken(String token) {
        return identityClient.authorizationToken(IntrospectRequest.builder()
                        .client_id(clientId)
                        .client_secret(clientSecret)
                        .token(token)
                .build());
    }

    public Mono<String> createUser(TokenExchangeResponse token, RegistrationRequest request) {
        return identityClient.createUser(
                token.getAccessToken(),
                UserCreationParam.builder()
                        .username(request.getUsername())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .enabled(true)
                        .emailVerified(false)
                        .credentials(List.of(Credential.builder()
                                .type("password")
                                .temporary(false)
                                .value(request.getPassword())
                                .build()))
                        .build())
                .doOnSuccess(userId -> log.info("User created with ID: {}", userId))
                .doOnError(e -> log.error("Error creating user: {}", e.getMessage()));
    }

    private Mono<ProfileResponse> saveProfile(String userId, RegistrationRequest request) {
        return Mono.fromCallable(() -> {
                    Profile profile = profileMapper.toProfile(request);
                    profile.setUserId(userId);
                    return profile;
                })
                .flatMap(profileRepository::save)
                .map(profileMapper::toProfileResponse)
                .doOnSuccess(profile -> log.info("Profile saved successfully: {}", profile))
                .doOnError(e -> log.error("Error saving profile: {}", e.getMessage()));
    }

    private Mono<ProfileResponse> handleWebClientError(WebClientResponseException ex) {
        log.error("Error occurred during profile creation: {}", ex.getMessage());
        return Mono.error(new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
    }

    public Mono<ProfileResponse> updateProfile(String profileId, Mono<ProfileUpdateRequest> request) {
        return profileRepository.findById(profileId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.USER_NOT_EXISTED)))
                .zipWith(request)
                .map(tuple -> {
                    Profile profile = tuple.getT1();
                    ProfileUpdateRequest profileUpdateRequest = tuple.getT2();
                    return updateProfileds(profile, profileUpdateRequest);
                })
                .flatMap(profile -> profileRepository.save(profile))
                .map(profileMapper::toProfileResponse)
                .doOnNext(profileResponse -> log.info("Profile update success: {}", profileResponse))
                .doOnError(throwable -> new AppException(ErrorCode.UPDDATE_PROFILE_IT_FOUND));
    }

    public Profile updateProfileds(Profile profile, ProfileUpdateRequest request) {
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }

        if (request.getDob() != null) {
            profile.setDob(request.getDob());
        }

        return profile;
    }

    public Mono<ProductResponse> fetchProductResponse(String id, String token) {
        return productClient.fetchProductResponse(id,token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Flux<ProfileResponse> loadAllProfile() {
        return profileRepository.findAll()
                .map(profileMapper::toProfileResponse);
    }

    public Mono<ProfileResponse> getProfile(String profileId) {
        return profileRepository.findById(profileId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PROFLiE_NOT_EXISTED)))
                .map(profileMapper::toProfileResponse);
    }

    public Mono<Void> deleteProfile(String profileId) {
        return profileRepository.deleteById(profileId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PROFLiE_NOT_EXISTED)));
    }



    //    public Mono<ProfileResponse> enrichProfileResponse(ProfileResponse response, String token) {
//        return Mono.just(response)
//                .flatMap(profileResponse -> profileRepository.findById(profileResponse.getProfileId())
//                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.PROFLiE_NOT_EXISTED)))
//                        .flatMap(profile -> Mono.zip(
//                                selectedCartItemResponse(profile.getCartItem(), token),
//                                selectedOrdersResponse(profile.getOrders(), token)
//                        )).map(tuple -> {
//                            profileResponse.setCartItem(tuple.getT1());
//                            profileResponse.setOrders(tuple.getT2());
//
//                            return profileResponse;
//                        })
//                );
//    }



//    public Mono<Set<CartItemResponse>> selectedCartItemResponse(Set<CartItem> cartItems, String token) {
//        return Flux.fromIterable(cartItems)
//                .flatMap(cartItem -> cartItemRepository.findById(cartItem.getCartItemId())
//                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.CART_ITEM_NOT_EXISTED))))
//                .flatMap(cartItem -> Mono.zip(
//                        Mono.just(cartItemMapper.toCartItemResponse(cartItem)),
//                        fetchProductResponse(cartItem.getProductId(), token)
//                )).map(tuple -> {
//                    CartItemResponse cartItemResponse = tuple.getT1();
//                    ProductResponse productResponse = tuple.getT2();
//
//                    cartItemResponse.setProductId(productResponse);
//                    return cartItemResponse;
//                })
//                .collect(Collectors.toSet());
//    }
//
//    public Mono<Set<OrdersResponse>> selectedOrdersResponse(Set<Orders> orders, String token) {
//        return Flux.fromIterable(orders)
//                .flatMap(orders1 -> ordersRepository.findById(orders1.getOrderId())
//                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.ORDERS_NOT_EXISTED))))
//                .flatMap(orders1 -> Mono.zip(
//                        Mono.just(ordersMapper.toOrdersResponse(orders1)),
//                        SelectedProductResponse(orders1.getSelectedProducts(), token)
//                )).map(tuple -> {
//                    OrdersResponse ordersResponse = tuple.getT1();
//                    ordersResponse.setSelectedProducts(tuple.getT2());
//                    return ordersResponse;
//                })
//                .collect(Collectors.toSet());
//    }
//
//    public Mono<Set<SelectedProductResponse>> SelectedProductResponse(Set<SelectedProduct> selectedProducts, String token) {
//        if (selectedProducts == null || selectedProducts.isEmpty()) {
//            return Mono.just(new HashSet<>());
//        }
//
//        return Flux.fromIterable(selectedProducts)
//                .flatMap(selectedProduct -> selectedProductRepository.findById(selectedProduct.getSelectedId())
//                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.SELECTED_PRODUCT_NOT_EXISTED)))
//                        .zipWith(fetchProductResponse(selectedProduct.getProductId(), token)))
//                .map(tuple2 -> {
//                    SelectedProductResponse selectedProductResponse = selectedProductMapper.toSelectedProductResponse(tuple2.getT1());
//                    selectedProductResponse.setProductId(tuple2.getT2());
//                    return selectedProductResponse;
//                }).collect(Collectors.toSet());
//    }
}
