package com.example.profile_service.dto.selected.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SelectedProductResponse {
    String selectedId;
    int quantity;
    String productId;
}
