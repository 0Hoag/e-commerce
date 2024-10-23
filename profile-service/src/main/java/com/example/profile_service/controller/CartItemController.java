package com.example.profile_service.controller;


import com.example.profile_service.dto.cartItem.cartItemRequest.AddCartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemRequest.CartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemRequest.RemoveCartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemRequest.UpdateCartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemResponse.CartItemResponse;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.service.CartItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/cartItem")
public class CartItemController {
    CartItemService cartService;

    @GetMapping("/getAllCartItem")
    public Flux<CartItemResponse> getAllCartItem() {
        return cartService.getAllCartItem();
    }

    @PostMapping("/registration")
    public Mono<CartItemResponse> createCartItem(
            @RequestBody Mono<CartItemRequest> request,
            @RequestHeader("Authorization") String token) {
        return cartService.createCartItem(request, token);
    }

    @GetMapping("/{cartItemId}")
    public Mono<CartItemResponse> getCartItem(@PathVariable String cartItemId) {
        return cartService.getCartItem(cartItemId);
    }

    @PostMapping("/addCart/{profileId}")
    public Mono<Profile> addCart(
            @PathVariable String profileId,
            @RequestBody AddCartItemRequest request) {
        return cartService.addCartItemToProfile(profileId, request);
    }

    @DeleteMapping("/removeCart/{profileId}")
    public Mono<Profile> removeCart(
            @PathVariable String profileId,
            @RequestBody RemoveCartItemRequest request
            ) {
        return cartService.removeCartItemToProfile(profileId, request);
    }

    @PutMapping("/updateCartItem/{cartItemId}")
    public Mono<CartItemResponse> updateCartItem(
            @PathVariable String cartItemId,
            @RequestBody UpdateCartItemRequest request) {
        return cartService.updateCartItem(cartItemId, request);
    }

    @DeleteMapping("/{cartItemId}")
    public Mono<Void> deleteCartItem(@PathVariable String cartItemId) {
        return cartService.deleteCartItem(cartItemId);
    }
}
