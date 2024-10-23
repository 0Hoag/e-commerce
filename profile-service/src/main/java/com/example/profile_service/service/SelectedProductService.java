package com.example.profile_service.service;

import com.example.profile_service.dto.ProductResponse;
import com.example.profile_service.dto.selected.request.AddSelectedProductRequest;
import com.example.profile_service.dto.selected.request.SelectedProductRequest;
import com.example.profile_service.dto.selected.response.SelectedProductResponse;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.entity.SelectedProduct;
import com.example.profile_service.exception.AppException;
import com.example.profile_service.exception.ErrorCode;
import com.example.profile_service.mapper.SelectedProductMapper;
import com.example.profile_service.repository.OrdersRepository;
import com.example.profile_service.repository.ProfileRepository;
import com.example.profile_service.repository.SelectedProductRepository;
import com.example.profile_service.repository.webClient.IdentityClient;
import com.example.profile_service.repository.webClient.ProductClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SelectedProductService {
    SelectedProductRepository selectedProductRepository;
    OrdersRepository ordersRepository;
    SelectedProductMapper selectedProductMapper;
    ProfileRepository profileRepository;
    ProductClient productClient;
    IdentityClient identityClient;

    public Mono<SelectedProductResponse> toCreate(Mono<SelectedProductRequest> request, String token) {
        return request
                .flatMap(request1 -> {
                    return profileRepository.findById(request1.getProfileId())
                            .switchIfEmpty(Mono.error(new AppException(ErrorCode.PROFLiE_NOT_EXISTED)))
                            .zipWith(fetchProductResponse(request1.getProductId(), token))
                            .flatMap(tuple -> {
                                SelectedProduct selectedProduct = selectedProductMapper.toSelectedProduct(request1);
                                Profile profile = tuple.getT1();
                                ProductResponse productResponse = tuple.getT2();

                                selectedProduct.setProfile(profile);
                                selectedProduct.setProductId(productResponse.getId());
                                return selectedProductRepository.save(selectedProduct)
                                        .map(selectedProductMapper::toSelectedProductResponse)
                                        .doOnNext(response -> log.info("Created SelectedProduct: {}", response))
                                        .doOnError(throwable -> log.error("Failed to create SelectedProduct: ", throwable));
                            });
                })
                .doOnNext(response -> log.info("Successfully created SelectedProduct"))
                .doOnError(throwable -> log.error("Failed to create SelectedProduct: ", throwable));
    }

    public Mono<Void> addSelectedByOrdersResponse(String ordersId, AddSelectedProductRequest request) {
        return ordersRepository.findById(ordersId)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.ORDERS_NOT_EXISTED)))
                .flatMap(orders -> selectedProductRepository.findAllById(request.getSelectedId())
                        .collectList()
                        .flatMap(selectedProducts -> {
                            orders.getSelectedProducts().addAll(selectedProducts);
                            ordersRepository.save(orders);
                            return null;
                        })
                );
    }

    public Flux<SelectedProductResponse> loadAllSelectedProduct(String token) {
        return selectedProductRepository.findAll()
                .flatMap(selectedProduct ->
                        fetchProductResponse(selectedProduct.getProductId(), token)
                            .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                            .map(tuple -> {
                                SelectedProductResponse selectedProductResponse = selectedProductMapper.toSelectedProductResponse(selectedProduct);
                                selectedProductResponse.setProductId(tuple.getId());

                                return selectedProductResponse;
                            })
                ).doOnNext(__ -> log.info("Create selected successfully"))
                .doOnError(e -> log.error("Error create selected info: {}", e.getMessage()));
    }

    public Mono<ProductResponse> fetchProductResponse(String id, String token) {
        return productClient.fetchProductResponse(id, token);
    }

//    public Mono<Set<SelectedProductResponse>> SelectedProductToOrdersResponse(Set<SelectedProduct> selectedProducts, String token) {
//        return Mono.just(selectedProducts)
//                .flatMap(selectedProducts1 -> Flux.fromIterable(selectedProducts1)
//                        .flatMap(selectedProduct -> selectedProductRepository.findById(selectedProduct.getSelectedId())
//                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.SELECTED_PRODUCT_NOT_EXISTED)))
//                                .zipWith(fetchProductResponse(selectedProduct.getProductId(), token))
//                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
//                        ).map(tuple -> {
//                            SelectedProductResponse selectedProductResponse = selectedProductMapper.toSelectedProductResponse(tuple.getT1());
//                            ProductResponse productResponse = tuple.getT2();
//                            selectedProductResponse.setProductId(productResponse);
//
//                            return selectedProductResponse;
//                        }).collect(Collectors.toSet())
//                ).doOnNext(response -> log.info("SelectedProductToOrdersResponse success fully!"))
//                .doOnError(throwable -> log.error("SelectedProductToOrdersResponse it found!"));
//    }
}
