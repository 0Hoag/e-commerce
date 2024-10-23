package com.example.profile_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "orders")
public class Orders {
    @MongoId
    String orderId;

    String fullName;
    String phoneNumber;
    String country;
    String city;
    String district;
    String ward;
    String address;
    String paymentMethod;

    String vnpTxnRef;
    String vnpOrderInfo;
    BigDecimal vnpAmount;
    String vnpResponseCode;
    String vnpTransactionNo;
    String vnpPayDate;
    String vnpTransactionStatus;

    Set<SelectedProduct> selectedProducts;

    Profile profile;

    @Override
    public String toString() {
        return "Orders{" + "orderId='"
                + orderId + '\'' + ", fullName='"
                + fullName + '\'' + ", phoneNumber='"
                + phoneNumber + '\'' + ", country='"
                + country + '\'' + ", city='"
                + city + '\'' + ", district='"
                + district + '\'' + ", ward='"
                + ward + '\'' + ", address='"
                + address + '\'' + ", vnpTxnRef='"
                + vnpTxnRef + '\'' + ", vnpAmount='"
                + vnpAmount + '\'' + ", vnpResponseCode='"
                + vnpResponseCode + '\'' + ", vnpTransactionNo='"
                + vnpTransactionNo + '\'' + ", vnpPayDate='"
                + vnpPayDate + '\'' + ", vnpTransactionStatus='"
                + vnpTransactionStatus + '\'' + '}';
    }
}