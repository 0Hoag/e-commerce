package com.example.profile_service.controller;

import com.example.profile_service.dto.vn_pay.VNPayDTO;
import com.example.profile_service.mapper.OrdersMapper;
import com.example.profile_service.service.OrdersService;
import com.example.profile_service.service.VNpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
@RequiredArgsConstructor
@Slf4j
public class VNPayController {
    private final VNpayService vnPayService;
    private final OrdersService ordersService;

    @PostMapping("/create-payment")
    public Mono<ServerResponse> createPayment(ServerRequest request) {
        return request.queryParam("orderId")
                .map(orderId -> {
                    log.info("Received create payment request for order: {}", orderId);
                    return ordersService.getOrders(orderId)
                            .flatMap(order ->
                                    vnPayService.getClientIpAddress(request)
                                            .flatMap(ipAddress ->
                                                    vnPayService.createPaymentUrl(order, ipAddress)
                                            )
                            )
                            .flatMap(vnpayment ->
                                    ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(vnpayment)
                            );
                })
                .orElse(ServerResponse.badRequest().build())
                .onErrorResume(e -> {
                    log.error("Error creating payment: ", e);
                    return ServerResponse.status(500)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("An error occurred while creating the payment: " + e.getMessage());
                });
    }

    @GetMapping("/payment-return")
    public Mono<ServerResponse> handlePaymentReturn(ServerRequest request) {
        return vnPayService.processReturnUrl(request)
                .flatMap(responseDTO -> {
                    boolean isSuccess = "00".equals(responseDTO.getResponseCode()) && "00".equals(responseDTO.getTransactionStatus());
                    String statusMessage = isSuccess ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED";

                    return ordersService.updateVNPayResponse(responseDTO.getTxnRef(), responseDTO)
                            .then(Mono.defer(() -> {
                                if ("PAYMENT_FAILED".equals(statusMessage)) {
                                    return ordersService.deleteOrders(responseDTO.getTxnRef());
                                }
                                return Mono.empty();
                            }))
                            .thenReturn(responseDTO);
                })
                .flatMap(responseDTO ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(responseDTO)
                )
                .onErrorResume(e -> {
                    log.error("Error processing payment return: ", e);
                    return ServerResponse.status(500)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("An error occurred while processing the payment return: " + e.getMessage());
                });
    }
}