package com.example.profile_service.dto.orders.OrdersRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrdersRequest {
    String fullName;
    String phoneNumber;
    String country;
    String city;
    String district;
    String ward;
    String address;
    String paymentMethod;

    String profileId;
}
