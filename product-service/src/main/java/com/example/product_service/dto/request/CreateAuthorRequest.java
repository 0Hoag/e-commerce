package com.example.product_service.dto.request;

import com.example.product_service.dto.response.ProductResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAuthorRequest {
    String name;
    Set<String> product;
}
