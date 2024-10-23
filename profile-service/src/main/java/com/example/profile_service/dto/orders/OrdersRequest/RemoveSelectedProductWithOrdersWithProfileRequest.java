package com.example.profile_service.dto.orders.OrdersRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RemoveSelectedProductWithOrdersWithProfileRequest {
    Set<String> orderId;
}
