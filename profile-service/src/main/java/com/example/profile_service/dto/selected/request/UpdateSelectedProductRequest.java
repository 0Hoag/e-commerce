package com.example.profile_service.dto.selected.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSelectedProductRequest {
    String productId;
    int quantity;
}
