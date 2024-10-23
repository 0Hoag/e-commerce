package com.example.product_service.mapper;

import com.example.product_service.dto.request.CategoryRequest;
import com.example.product_service.dto.request.CategoryUpdateRequest;
import com.example.product_service.dto.response.CategoryResponse;
import com.example.product_service.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category entity);

    void updateCategory(@MappingTarget Category profile, CategoryUpdateRequest request);
}
