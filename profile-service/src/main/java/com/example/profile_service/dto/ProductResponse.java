package com.example.profile_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String author; // author id
    String name;
    String address;
    double listedPrice;
    double price;
    int quantity;
    int stockQuantity;
    String description;
    Set<String> image;
}
