package com.example.profile_service.service;

import com.example.profile_service.dto.vn_pay.VNPayDTO;
import com.example.profile_service.dto.vn_pay.VNPayResponseDTO;
import com.example.profile_service.entity.Orders;
import com.example.profile_service.exception.AppException;
import com.example.profile_service.exception.ErrorCode;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VNpayService {

    @NonFinal
    @Value("${vnpay.payment-url}")
    String vnpPaymentUrl;

    @NonFinal
    @Value("${vnpay.tmn-code}")
    String vnpTmnCode;

    @NonFinal
    @Value("${vnpay.hash-secret}")
    String vnpHashSecret;

    public Mono<VNPayDTO> createPaymentUrl(Orders orders, String ipAddress) {
        return Mono.fromCallable(() -> {
            String vnp_TxnRef = orders.getOrderId();
            String vnp_OrderInfo = StringUtils.isNotBlank(orders.getVnpOrderInfo())
                    ? orders.getVnpOrderInfo()
                    : "Thanh toan don hang: " + vnp_TxnRef;
            String vnp_OrderType = "billpayment";
            BigDecimal amount = Optional.ofNullable(orders.getVnpAmount()).orElse(BigDecimal.ZERO);
            String vnp_Amount = amount.multiply(new BigDecimal("100")).toBigInteger().toString();
            String vnp_Locale = "vn";
            String vnp_ReturnUrl = "http://localhost:3000/payment-result";

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnpTmnCode);
            vnp_Params.put("vnp_Amount", vnp_Amount);
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", vnp_OrderType);
            vnp_Params.put("vnp_Locale", vnp_Locale);
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", ipAddress);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            String queryUrl = VNPayUtils.createQueryUrl(vnp_Params);
            String vnp_SecureHash = VNPayUtils.calculateHmacSHA512(vnpHashSecret, queryUrl);
            String paymentUrl = vnpPaymentUrl + "?" + queryUrl + "&vnp_SecureHash=" + vnp_SecureHash;

            return new VNPayDTO(vnp_TxnRef, vnp_OrderInfo, new BigDecimal(vnp_Amount), paymentUrl);
        }).onErrorResume(e -> {
            log.info("Error creating payment URL: ", e);
            return Mono.error(new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        });
    }

    public Mono<VNPayResponseDTO> processReturnUrl(ServerRequest request) {
        return Mono.fromCallable(() -> {
            MultiValueMap<String, String> queryParams = request.queryParams();

            String vnp_ResponseCode = queryParams.getFirst("vnp_ResponseCode");
            String vnp_TxnRef = queryParams.getFirst("vnp_TxnRef");
            String vnp_TransactionNo = queryParams.getFirst("vnp_TransactionNo");
            String vnp_OrderInfo = queryParams.getFirst("vnp_OrderInfo");
            String vnp_PayDate = queryParams.getFirst("vnp_PayDate");
            String vnp_TransactionStatus = queryParams.getFirst("vnp_TransactionStatus");

            if (vnp_ResponseCode == null || vnp_TxnRef == null) {
                log.error("Missing required parameters in VNPay response");
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }

            return new VNPayResponseDTO(
                    vnp_TxnRef,
                    vnp_OrderInfo,
                    vnp_ResponseCode,
                    vnp_TransactionNo,
                    vnp_PayDate,
                    ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus) ? "00" : "Failed")
            );
        }).onErrorResume(e -> {
            log.error("Error processing VNPay return URL: ", e);
            return Mono.error(new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        });
    }

    public Mono<String> getClientIpAddress(ServerRequest request) {
        return Mono.just(request.remoteAddress()
                .map(address -> address.getAddress().getHostAddress())
                .orElse("Unknown"));
    }

    public static class VNPayUtils {
        public static String createQueryUrl(Map<String, String> params) {
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);
            StringBuilder queryUrl = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();

            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    queryUrl.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    queryUrl.append('=');
                    queryUrl.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                    if (itr.hasNext()) {
                        queryUrl.append("&");
                    }
                }
            }
            return queryUrl.toString();
        }

        public static String calculateHmacSHA512(String key, String data) {
            try {
                Mac sha512Hmac = Mac.getInstance("HmacSHA512");
                SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
                sha512Hmac.init(secretKeySpec);
                byte[] hmacData = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
                return bytesToHex(hmacData);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException("Error calculating HMAC-SHA512", e);
            }
        }

        private static String bytesToHex(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
    }
}