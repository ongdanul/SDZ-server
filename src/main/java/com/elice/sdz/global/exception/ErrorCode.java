package com.elice.sdz.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 상품 관련 에러
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "The product does not exist."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "OUT_OF_STOCK", "The product is out of stock."),

    // 주문 관련 에러
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "The order does not exist."),
    INVALID_ORDER(HttpStatus.BAD_REQUEST, "INVALID_ORDER", "The order is invalid."),

    // 회원 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "The user does not exist."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password."),

    // 결제 관련 에러
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT_FAILED", "Payment processing failed."),
    PAYMENT_METHOD_INVALID(HttpStatus.BAD_REQUEST, "PAYMENT_METHOD_INVALID", "Invalid payment method."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
