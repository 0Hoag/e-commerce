package com.example.profile_service.mapper;


import com.example.profile_service.dto.request.ProfileUpdateRequest;
import com.example.profile_service.dto.request.RegistrationRequest;
import com.example.profile_service.dto.response.ProfileResponse;
import com.example.profile_service.entity.CartItem;
import com.example.profile_service.entity.Orders;
import com.example.profile_service.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {CartItemMapper.class, SelectedProductMapper.class, OrdersMapper.class})
public interface ProfileMapper {

    @Mapping(target = "cartItem", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Profile toProfile(RegistrationRequest request);

    @Mapping(source = "cartItem", target = "cartItem", qualifiedByName = "mapCartItemsToIds")
    @Mapping(source = "orders", target = "orders", qualifiedByName = "mapOrdersToIds")
    ProfileResponse toProfileResponse(Profile profile);

    @Mapping(target = "cartItem", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateUser(@MappingTarget Profile profile, ProfileUpdateRequest request);

    @Named("mapCartItemsToIds")
    default Set<String> mapCartItemsToIds(Set<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return new HashSet<>();
        }

        return cartItems.stream()
                .map(CartItem::getCartItemId)
                .collect(Collectors.toSet());
    }

    @Named("mapOrdersToIds")
    default Set<String> mapOrdersToIds(Set<Orders> orders) {
        if (orders == null || orders.isEmpty()) {
            return new HashSet<>();
        }

        return orders.stream()
                .map(Orders::getOrderId)
                .collect(Collectors.toSet());
    }
}
