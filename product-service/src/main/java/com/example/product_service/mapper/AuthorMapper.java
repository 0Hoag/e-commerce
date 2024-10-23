package com.example.product_service.mapper;

import com.example.product_service.dto.request.CreateAuthorRequest;
import com.example.product_service.dto.request.CreateProductRequest;
import com.example.product_service.dto.request.UpdateAuthorRequest;
import com.example.product_service.dto.response.AuthorResponse;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.Author;
import com.example.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    @Mapping(target = "product", source = "product", qualifiedByName = "mapIdsToProducts")
    Author toAuthor(CreateAuthorRequest request);

    AuthorResponse toAuthorResponse(Author entity);

    void updateAuthor(@MappingTarget Author author, UpdateAuthorRequest request);

    @Named("mapIdsToProducts")
    default Set<Product> mapIdsToProducts(Set<String> productIds) {
        if (productIds == null) {
            return null;
        }
        return productIds.stream()
                .map(id -> {
                    Product product = new Product();
                    product.setId(id);
                    return product;
                })
                .collect(Collectors.toSet());
    }

    default String mapProductToId(Product product) {
        return product != null ? product.getId() : null;
    }
}