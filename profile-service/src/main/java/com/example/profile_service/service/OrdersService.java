package com.example.profile_service.service;

import com.example.profile_service.dto.ProductResponse;
import com.example.profile_service.dto.orders.OrdersRequest.AddSelectedProductWithOrdersWithProfileRequest;
import com.example.profile_service.dto.orders.OrdersRequest.CreateOrdersRequest;
import com.example.profile_service.dto.orders.OrdersRequest.RemoveSelectedProductWithOrdersWithProfileRequest;
import com.example.profile_service.dto.orders.OrdersRequest.UpdateOrdersRequest;
import com.example.profile_service.dto.orders.OrdersResponse.OrdersResponse;
import com.example.profile_service.dto.vn_pay.VNPayResponseDTO;
import com.example.profile_service.entity.Orders;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.entity.SelectedProduct;
import com.example.profile_service.exception.AppException;
import com.example.profile_service.exception.ErrorCode;
import com.example.profile_service.mapper.OrdersMapper;
import com.example.profile_service.mapper.ProfileMapper;
import com.example.profile_service.mapper.SelectedProductMapper;
import com.example.profile_service.repository.OrdersRepository;
import com.example.profile_service.repository.ProfileRepository;
import com.example.profile_service.repository.SelectedProductRepository;
import com.example.profile_service.repository.webClient.ProductClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersService {
    ProductClient productClient;
    OrdersRepository ordersRepository;
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    OrdersMapper ordersMapper;
    SelectedProductRepository selectedProductRepository;
    SelectedProductMapper selectedProductMapper;

    public Mono<Orders> createOrders(Mono<CreateOrdersRequest> request, String token) {
        return request
                .flatMap(createOrdersRequest -> profileRepository.findById(createOrdersRequest.getProfileId())
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.ORDERS_NOT_EXISTED)))
                        .map(profile -> Tuples.of(createOrdersRequest, profile))
                ).flatMap(tuple -> {
                    CreateOrdersRequest createOrdersRequest = tuple.getT1();
                    Profile profile = tuple.getT2();

                    Orders orders = ordersMapper.toOrders(createOrdersRequest);
                    orders.setProfile(profile);

                    return selectedProductRepository.findAllById(createOrdersRequest.getSelectedProducts())
                            .flatMap(selectedProduct -> {
                                return productClient.fetchProductResponse(selectedProduct.getProductId(), token)
                                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                                        .map(productResponse -> Tuples.of(selectedProduct, productResponse));
                            })
                            .reduce(Tuples.of(new HashSet<SelectedProduct>(), 0.0),
                                    (acc, tuple2) -> {
                                        SelectedProduct selectedProduct = tuple2.getT1();
                                        ProductResponse productResponse = tuple2.getT2();

                                        acc.getT1().add(selectedProduct);
                                        double totalAmount = acc.getT2() + (selectedProduct.getQuantity() * productResponse.getPrice());
                                        return Tuples.of(acc.getT1(), totalAmount);
                                    })
                            .flatMap(response -> {
                                orders.setSelectedProducts(response.getT1());
                                orders.setVnpAmount(BigDecimal.valueOf(response.getT2()));
                                return ordersRepository.save(orders);
                            });
                })
                .doOnNext(response -> log.info("Create Orders success fully!"))
                .doOnError(throwable -> log.error("Create Orders it found!"));
    }


    public Mono<Profile> addOrdersToProfile(String profileId, AddSelectedProductWithOrdersWithProfileRequest request) {
        return profileRepository.findById(profileId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PROFLiE_NOT_EXISTED)))
                .flatMap(profile -> ordersRepository.findAllById(request.getOrderId())
                        .collectList()
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.ORDERS_NOT_EXISTED)))
                        .flatMap(selectedProducts -> {
                            profile.getOrders().addAll(selectedProducts);
                            return profileRepository.save(profile);
                        }))
                .doOnNext(response -> log.info("Create Orders success fully!"))
                .doOnError(throwable -> log.error("Create Orders it found!"));
    }

    public Mono<Profile> removeOrdersToProfile(String profileId, RemoveSelectedProductWithOrdersWithProfileRequest request) {
        return profileRepository.findById(profileId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PROFLiE_NOT_EXISTED)))
                .flatMap(profile -> ordersRepository.findAllById(request.getOrderId())
                        .collectList()
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.ORDERS_NOT_EXISTED)))
                        .flatMap(ordersList -> {
                            List<SelectedProduct> selectedProductsToDelete = ordersList.stream()
                                    .flatMap(order -> order.getSelectedProducts().stream())
                                    .collect(Collectors.toList());
                            profile.getOrders().removeAll(ordersList);

                            return selectedProductRepository.deleteAll(selectedProductsToDelete)
                                    .then(ordersRepository.deleteAll(ordersList))
                                    .then(profileRepository.save(profile));
                        }))
                .doOnNext(response -> log.info("Remove Orders successfully!"))
                .doOnError(throwable -> log.error("Error occurred while removing Orders!"));
    }

    public Mono<Void> deleteOrders(String orderId) {
        return ordersRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.ORDERS_NOT_EXISTED)))
                .flatMap(orders -> {
                    if (orders.getSelectedProducts() != null && !orders.getSelectedProducts().isEmpty()) {
                        return selectedProductRepository.deleteAll(orders.getSelectedProducts())
                                .then(ordersRepository.deleteById(orderId));
                    } else {
                        return ordersRepository.deleteById(orderId);
                    }
                });
    }

    public Flux<OrdersResponse> getAllOrder() {
        return ordersRepository.findAll()
                .map(ordersMapper::toOrdersResponse);
    }

    public Mono<Orders> getOrders(String orderId) {
        return ordersRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.ORDERS_NOT_EXISTED)));
    }

    public Mono<OrdersResponse> updateOrders(String orderId, UpdateOrdersRequest request) {
        return ordersRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.ORDERS_NOT_EXISTED)))
                .flatMap(orders -> {
                    ordersMapper.updateOrder(orders, request);
                    return ordersRepository.save(orders);
                }).map(ordersMapper::toOrdersResponse);
    }

    public Mono<Orders> updateVNPayResponse(String orderId, VNPayResponseDTO responseDTO) {
        return ordersRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.ORDERS_NOT_EXISTED)))
                .flatMap(orders -> {
                    orders.setVnpTxnRef(orderId);
                    orders.setVnpOrderInfo(responseDTO.getOrderInfo());
                    orders.setVnpResponseCode(responseDTO.getResponseCode());
                    orders.setVnpTransactionNo(responseDTO.getTransactionNo());
                    orders.setVnpPayDate(responseDTO.getPayDate());
                    orders.setVnpTransactionStatus(responseDTO.getTransactionStatus());

                    if ("00".equals(responseDTO.getResponseCode()) && "00".equals(responseDTO.getTransactionStatus())) {
                        orders.setVnpTransactionStatus("PAYMENT_SUCCESS");
                    }else {
                        orders.setVnpTransactionStatus("PAYMENT_FAILED");
                    }

                    return ordersRepository.save(orders);
                });
    }

    public Mono<ProductResponse> fetchProductResponse(String id, String token) {
        return productClient.fetchProductResponse(id, token);
    }

//    public Mono<Set<SelectedProductResponse>> SelectedProductToOrdersResponse(Set<SelectedProduct> selectedProducts, String token) {
//        if (selectedProducts == null || selectedProducts.isEmpty()) {
//            return Mono.just(new HashSet<>());
//        }
//
//        return Flux.fromIterable(selectedProducts)
//                .flatMap(selectedProduct -> selectedProductRepository.findById(selectedProduct.getSelectedId())
//                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.SELECTED_PRODUCT_NOT_EXISTED)))
//                        .zipWith(fetchProductResponse(selectedProduct.getProductId(), token))
//                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
//                ).map(tuple -> {
//                    SelectedProductResponse selectedProductResponse = selectedProductMapper.toSelectedProductResponse(tuple.getT1());
//                    selectedProductResponse.setProductId(tuple.getT2());
//                    return selectedProductResponse;
//                }).collect(Collectors.toSet())
//                .doOnNext(response -> log.info("Create Orders success fully!"))
//                .doOnError(throwable -> log.error("Create Orders it found!"));
//    }
}
