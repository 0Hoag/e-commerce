package com.example.product_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {
    String name;
    String author; // author id
    String address;
    double listedPrice;
    double price;
    int quantity;
    int stockQuantity;
    String description;

    List<String> categoryIds;
}
