package com.example.profile_service.mapper;



import com.example.profile_service.dto.selected.request.SelectedProductRequest;
import com.example.profile_service.dto.selected.request.UpdateSelectedProductRequest;
import com.example.profile_service.dto.selected.response.SelectedProductResponse;
import com.example.profile_service.entity.SelectedProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SelectedProductMapper {
    @Mappings({@Mapping(target = "productId", ignore = true)})
    SelectedProduct toSelectedProduct(SelectedProductRequest request);

//    @Mappings({@Mapping(source = "productId", target = "productId", qualifiedByName = "mapProductIdToBookResponse")})
    SelectedProductResponse toSelectedProductResponse(SelectedProduct selectedProduct);

    void updateSelectedProduct(@MappingTarget SelectedProduct selectedProduct, UpdateSelectedProductRequest request);

//    @Named("mapProductIdToBookResponse")
//    default ProductResponse mapProductIdToBookResponse(String productId) {
//        return ProductResponse.builder().id(productId).build();
//    }
}
