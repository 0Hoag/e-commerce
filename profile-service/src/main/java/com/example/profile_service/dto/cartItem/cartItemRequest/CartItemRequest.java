package com.example.profile_service.dto.cartItem.cartItemRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequest {
    int quantity;
    String productId;

    String profileId;
}


