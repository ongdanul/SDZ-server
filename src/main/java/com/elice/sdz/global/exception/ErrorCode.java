package com.elice.sdz.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 상품 관련 에러
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "제품이 존재하지 않습니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "OUT_OF_STOCK", "제품이 품절되었습니다."),

    // 주문 관련 에러
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "주문이 존재하지 않습니다."),
    INVALID_ORDER(HttpStatus.BAD_REQUEST, "INVALID_ORDER", "주문이 잘못되었습니다."),
    ORDER_CANNOT_BE_MODIFIED(HttpStatus.BAD_REQUEST, "ORDER_CANNOT_BE_MODIFIED", "주문을 수정할 수 없습니다. 배송 처리가 시작되었거나 이미 완료된 주문입니다."),
    ORDER_CANNOT_BE_CANCELLED(HttpStatus.BAD_REQUEST, "ORDER_CANNOT_BE_CANCELLED", "주문을 취소할 수 없습니다. 배송 처리가 시작되었거나 이미 완료된 주문입니다."),
    // 회원 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자가 존재하지 않습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "잘못된 사용자 이름 또는 비밀번호입니다."),

    // 결제 관련 에러
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT_FAILED", "결제 처리에 실패했습니다."),

    // 카테고리 관련 에러
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "카테고리가 존재하지 않습니다."),
    CATEGORY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "CATEGORY_ALREADY_EXISTS", "카테고리 이름이 이미 존재합니다."),

    // 서버 관련 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "예기치 않은 오류가 발생했습니다."),

    // 장바구니 관련 에러
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_ITEM_NOT_FOUND", "장바구니가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
