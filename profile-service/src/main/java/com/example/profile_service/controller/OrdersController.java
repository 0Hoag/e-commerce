package com.example.profile_service.controller;

import com.example.profile_service.dto.orders.OrdersRequest.AddSelectedProductWithOrdersWithProfileRequest;
import com.example.profile_service.dto.orders.OrdersRequest.CreateOrdersRequest;
import com.example.profile_service.dto.orders.OrdersRequest.RemoveSelectedProductWithOrdersWithProfileRequest;
import com.example.profile_service.dto.orders.OrdersRequest.UpdateOrdersRequest;
import com.example.profile_service.dto.orders.OrdersResponse.OrdersResponse;
import com.example.profile_service.entity.Orders;
import com.example.profile_service.entity.Profile;
import com.example.profile_service.mapper.OrdersMapper;
import com.example.profile_service.service.OrdersService;
import com.example.profile_service.service.VNpayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/orders")
public class OrdersController {
    OrdersService ordersService;
    VNpayService vnPayService;
    OrdersMapper ordersMapper;

    @PostMapping("/registration")
    public Mono<OrdersResponse> registration(
            @RequestBody Mono<CreateOrdersRequest> request,
            @RequestHeader("Authorization") String token,
            ServerWebExchange exchange) {

        return request.flatMap(createOrdersRequest ->
                ordersService.createOrders(Mono.just(createOrdersRequest), token)
                        .flatMap(orders -> {
                            if ("VNPAY".equals(createOrdersRequest.getPaymentMethod())) {
                                String ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                                return vnPayService.createPaymentUrl(orders, ipAddress)
                                        .map(vnPayDTO -> {
                                            OrdersResponse ordersResponse = ordersMapper.toOrdersResponse(orders);
                                            ordersResponse.setPaymentUrl(vnPayDTO.getPaymentUrl());
                                            return ordersResponse;
                                        });
                            } else {
                                return Mono.just(ordersMapper.toOrdersResponse(orders));
                            }
                        })
        );
    }

    @PostMapping("/addSelectedProductWithOrdersWithProfile/{profileId}")
    public Mono<Profile> addSelectedProductWithOrdersWithUser(
            @PathVariable String profileId, @RequestBody AddSelectedProductWithOrdersWithProfileRequest request) {
        return ordersService.addOrdersToProfile(profileId, request);
    }

    @DeleteMapping("/removeSelectedProductWithOrdersWithUser/{profileId}")
    public Mono<Profile> removeSelectedProductWithOrdersWithUser(
            @PathVariable String profileId, @RequestBody RemoveSelectedProductWithOrdersWithProfileRequest request) {
        return ordersService.removeOrdersToProfile(profileId, request);
    }

    @PutMapping("/updateOrder/{orderId}")
    public Mono<OrdersResponse> updateOrders(
            @PathVariable String orderId,
            @RequestBody UpdateOrdersRequest request) {
        return ordersService.updateOrders(orderId, request);
    }

    @DeleteMapping("/deleteOrders/{orderId}")
    public Mono<Void> deleteOrders(@PathVariable String orderId) {
        return ordersService.deleteOrders(orderId);
    }

    @GetMapping("/getAllOrders")
    public Flux<OrdersResponse> getAllOrder() {
        return ordersService.getAllOrder();
    }

    @GetMapping("/getOrders/{orderId}")
    public Mono<Orders> getOrders(@PathVariable String orderId) {
        return ordersService.getOrders(orderId);
    }
}
