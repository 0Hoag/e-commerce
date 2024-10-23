package com.example.profile_service.mapper;



import com.example.profile_service.dto.ProductResponse;
import com.example.profile_service.dto.orders.OrdersRequest.CreateOrdersRequest;
import com.example.profile_service.dto.orders.OrdersRequest.UpdateOrdersRequest;
import com.example.profile_service.dto.orders.OrdersResponse.OrdersResponse;
import com.example.profile_service.dto.selected.response.SelectedProductResponse;
import com.example.profile_service.entity.Orders;
import com.example.profile_service.entity.SelectedProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrdersMapper {

    @Mapping(target = "selectedProducts", source = "selectedProducts", qualifiedByName = "mapSelectedProductsIdsToSelectedProducts")
    Orders toOrders(CreateOrdersRequest request);

    @Mapping(target = "selectedProducts", source = "selectedProducts", qualifiedByName = "mapSelectedProductToIds")
    OrdersResponse toOrdersResponse(Orders orders);

    void updateOrder(@MappingTarget Orders orders, UpdateOrdersRequest request);

    @Named("mapProductId")
    default String mapProductId(ProductResponse response) {
        return response.getId();
    }

    @Named("mapSelectedProductsIdsToSelectedProducts")
    default SelectedProduct mapSelectedProduct(String selectedId) {
        return SelectedProduct.builder().selectedId(selectedId).build();
    }

    @Named("mapSelectedProductToIds")
    default Set<String> mapSelectedProductToIds(Set<SelectedProduct> selectedProducts) {
        if (selectedProducts == null) {
            return null;
        }
        return selectedProducts.stream()
                .map(SelectedProduct::getSelectedId)
                .collect(Collectors.toSet());
    }

    @Named("mapSelectedProductResponse")
    default SelectedProductResponse mapSelectedProductResponse(String selectedId) {
        return SelectedProductResponse.builder().selectedId(selectedId).build();
    }
}

