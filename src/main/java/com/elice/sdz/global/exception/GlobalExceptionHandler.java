package com.elice.sdz.global.exception;

import com.elice.sdz.global.exception.category.CategoryAlreadyExistsException;
import com.elice.sdz.global.exception.category.CategoryNotFoundException;
import com.elice.sdz.global.exception.order.InvalidOrderException;
import com.elice.sdz.global.exception.order.OrderNotFoundException;
import com.elice.sdz.global.exception.payment.PaymentFailedException;
import com.elice.sdz.global.exception.product.OutOfStockException;
import com.elice.sdz.global.exception.product.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 모든 예외를 처리하는 기본 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        log.error("Global exception occurred");

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // 카테고리 관련 예외 처리(NOT_FOUND)
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException e) {
        log.error("Category not found exception occurred");

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.CATEGORY_NOT_FOUND);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 카테고리 관련 예외 처리(BAD_REQUEST)
    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCategoryAlreadyExistsException(CategoryAlreadyExistsException e) {
        log.error("Category already exists exception occurred");

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.CATEGORY_ALREADY_EXISTS);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 주문 관련 예외 처리(NOT_FOUND)
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException e) {
        log.error("Order not found exception occurred");

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.ORDER_NOT_FOUND);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 주문 관련 예외 처리(BAD_REQUEST)
    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderException(InvalidOrderException e) {
        log.error("Invalid order exception occurred");

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_ORDER);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 결제 관련 예외 처리(BAD_REQUEST)
    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailedException(PaymentFailedException e) {
        log.error("Payment failed exception occurred");

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.PAYMENT_FAILED);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 상품 관련 예외 처리(NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException e) {
        log.error("Product not found exception occurred");

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.PRODUCT_NOT_FOUND);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 상품 관련 예외 처리(BAD_REQUEST)
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStockException(OutOfStockException e) {
        log.error("Out of stock exception occurred");

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.OUT_OF_STOCK);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
