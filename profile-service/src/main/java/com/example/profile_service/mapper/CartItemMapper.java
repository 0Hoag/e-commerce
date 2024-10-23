package com.example.profile_service.mapper;

import com.example.profile_service.dto.cartItem.cartItemRequest.CartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemRequest.UpdateCartItemRequest;
import com.example.profile_service.dto.cartItem.cartItemResponse.CartItemResponse;
import com.example.profile_service.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mappings({@Mapping(target = "productId", ignore = true)})
    CartItem toCartItem(CartItemRequest request);

//    @Mappings({@Mapping(source = "productId", target = "productId", qualifiedByName = "mapProductIdToProductResponse")})
    CartItemResponse toCartItemResponse(CartItem cartItem);

    void updateCartItem(@MappingTarget CartItem cartItem, UpdateCartItemRequest request);

//    @Named("mapProductIdToProductResponse")
//    default ProductResponse mapProductIdToProductResponse(String bookId) {
//        return ProductResponse.builder().id(bookId).build();
//    }
}
