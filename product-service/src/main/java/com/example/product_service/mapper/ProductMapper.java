package com.example.product_service.mapper;

import com.example.product_service.dto.request.CreateProductRequest;
import com.example.product_service.dto.request.ProductUpdateRequest;
import com.example.product_service.dto.request.UpdateStockQuantityProductRequest;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(CreateProductRequest request);

    ProductResponse toProductResponse(Product entity);

    void updateStockQuantityProduct (@MappingTarget Product product, UpdateStockQuantityProductRequest request);

    void updateProduct (@MappingTarget Product product, ProductUpdateRequest request);
}
