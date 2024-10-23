package com.example.profile_service.service;

import com.example.profile_service.dto.cartItem.cartItemRequest.AddCartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemRequest.CartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemRequest.RemoveCartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemRequest.UpdateCartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemResponse.CartItemResponse;
import com.example.profile_service.entity.CartItem;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.exception.AppException;
import com.example.profile_service.exception.ErrorCode;
import com.example.profile_service.mapper.CartItemMapper;
import com.example.profile_service.mapper.ProfileMapper;
import com.example.profile_service.repository.CartItemRepository;
import com.example.profile_service.repository.ProfileRepository;
import com.example.profile_service.repository.webClient.ProductClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemService {
    CartItemMapper cartItemMapper;
    CartItemRepository cartItemRepository;
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    WebClient webClient;
    ProductClient productClient;

    @NonFinal
    @Value("${app.services.product}")
    String productService;

    public Flux<CartItemResponse> getAllCartItem() {
        return cartItemRepository.findAll()
                .map(cartItemMapper::toCartItemResponse);
    }

    public Mono<CartItemResponse> getCartItem(String cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.CART_ITEM_NOT_EXISTED)))
                .map(cartItemMapper::toCartItemResponse);
    }

    public Mono<CartItemResponse> createCartItem(Mono<CartItemRequest> request, String token) {
        return request
                .flatMap(cartItemRequest ->
                        profileRepository.findById(cartItemRequest.getProfileId())
                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PROFLiE_NOT_EXISTED)))
                                .map(profile -> Tuples.of(cartItemRequest, profile))
                ).flatMap(tuple -> {
                    CartItemRequest cartItemRequest = tuple.getT1();
                    Profile profile = tuple.getT2();

                    CartItem cartItem = cartItemMapper.toCartItem(cartItemRequest);
                    cartItem.setProfile(profile);
                    productClient.fetchProductResponse(cartItem.getProductId(), token)
                            .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)));
                    cartItem.setProductId(cartItemRequest.getProductId());
                    return cartItemRepository.save(cartItem);
                })
                .map(cartItemMapper::toCartItemResponse)
                .doOnNext(cartItemResponse -> log.info("Create cartItem successfully: {}", cartItemResponse))
                .doOnError(throwable -> log.error("Create cartItem it found: {}", throwable));

    }

    public Mono<Profile> addCartItemToProfile(String profileId, AddCartItemRequest request) {
        return profileRepository.findById(profileId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PROFLiE_NOT_EXISTED)))
                .flatMap(profile -> cartItemRepository.findAllById(request.getCartItemId())
                            .collectList()
                            .flatMap(cartItems -> {
                                profile.getCartItem().addAll(cartItems);
                                return profileRepository.save(profile);
                            })
                );
    }

    public Mono<Profile> removeCartItemToProfile(String profileId, RemoveCartItemRequest request) {
        return profileRepository.findById(profileId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PROFLiE_NOT_EXISTED)))
                .flatMap(profile -> cartItemRepository.findAllById(request.getCartItemId())
                        .collectList()
                        .flatMap(cartItems -> {
                            profile.getCartItem().removeAll(cartItems);
                            return cartItemRepository.deleteAll(cartItems)
                                    .then(profileRepository.save(profile));
                        })
                );
    }

    public Mono<CartItemResponse> updateCartItem(String cartItemId, UpdateCartItemRequest request) {
        return cartItemRepository.findById(cartItemId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.CART_ITEM_NOT_EXISTED)))
                .flatMap(cartItem -> {
                    cartItemMapper.updateCartItem(cartItem, request);
                    return cartItemRepository.save(cartItem);
                }).map(cartItemMapper::toCartItemResponse);
    }

//    public Mono<Set<CartItemResponse>> selectedCartItemResponse(Set<CartItem> cartItems, String token) {
//        return Mono.just(cartItems)
//                .flatMap(cartItems1 -> Flux.fromIterable(cartItems1)
//                        .flatMap(cartItem -> cartItemRepository.findById(cartItem.getProductId())
//                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.CART_ITEM_NOT_EXISTED)))
//                                .zipWith(productClient.fetchProductResponse(cartItem.getProductId(), token))
//                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
//                        ).map(tuple -> {
//                            CartItemResponse cartItemResponse = cartItemMapper.toCartItemResponse(tuple.getT1());
//                            ProductResponse productResponse = tuple.getT2();
//                            cartItemResponse.setProductId(productResponse.getId());
//
//                            return cartItemResponse;
//                        })
//                        .collect(Collectors.toSet())
//                ).doOnError(throwable -> log.info("Selected Product it found: ", throwable));
//    }

    public Mono<Void> deleteCartItem(String cartItemId) {
        return profileRepository.deleteById(cartItemId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.CART_ITEM_NOT_EXISTED)));
    }
}
