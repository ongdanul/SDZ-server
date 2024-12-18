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

    // 로그인 관련 에러
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 거부: 해당 리소스에 접근할 권한이 없습니다."),
    MISSING_AUTHORIZATION(HttpStatus.FORBIDDEN, "MISSING_AUTHORIZATION", "권한 정보가 없습니다."),
    LOGIN_LOCKED(HttpStatus.FORBIDDEN, "LOGIN_LOCKED", "로그인 잠금된 계정입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED", "로그인에 실패하였습니다."),

    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_ACCESS_TOKEN", "만료된 엑세스 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_REFRESH_TOKEN", "만료된 리프레시 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_ACCESS_TOKEN", "유효하지 않은 엑세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."),
    INVALID_REFRESH_COOKIE(HttpStatus.BAD_REQUEST, "INVALID_REFRESH_COOKIE", "리프레시 토큰이 쿠키에 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "REFRESH_TOKEN_NOT_FOUND", "DB에 리프레시 토큰이 존재하지 않습니다."),

    // 로그아웃 관련 에러
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "허용되지 않는 메소드입니다."),


    // 회원 가입 관련 에러
    OAUTH2_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "OAUTH2_AUTHENTICATION_FAILED", "지원되지 않는 가입유형입니다."),
    SOCIAL_USER_EXISTS(HttpStatus.BAD_REQUEST, "SOCIAL_USER_EXISTS", "해당 소셜 계정으로 이미 가입되어 있습니다."),
    LOCAL_USER_EXISTS(HttpStatus.BAD_REQUEST, "LOCAL_USER_EXISTS", "이미 일반 회원으로 가입되어 있습니다."),

    // 비밀번호 찾기 에러,
    MAIL_SEND_FAILED(HttpStatus.BAD_REQUEST, "MAIL_SEND_FAILED", "메일 전송 중 오류가 발생했습니다."),

    // 회원 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "회원이 존재하지 않습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "잘못된 회원 아이디 또는 비밀번호입니다."),
    SIGN_UP_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "SIGN_UP_LIMIT_EXCEEDED", "회원이 가입 할 수 있는 최대 계정 수를 초과하였습니다."),

    // 검색 관련 에러
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "INVALID_TYPE", "지원하지 않는 검색 조건입니다."),

    // 회원 삭제 관련 에러
    NO_USER_IDS_TO_DELETE(HttpStatus.NOT_FOUND, "NO_USER_IDS_TO_DELETE", "삭제할 회원 아이디 목록이 존재하지 않습니다."),
    USER_IDS_NOT_EXIST(HttpStatus.NOT_FOUND, "USER_IDS_NOT_EXIST", "삭제할 회원 아이디 중 일부가 존재하지 않습니다."),

    // 배송지 관련 에러
    DELIVERY_ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "DELIVERY_ADDRESS_NOT_FOUND", "배송지 정보가 존재하지 않습니다."),

    // 결제 관련 에러
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT_FAILED", "결제 처리에 실패했습니다."),

    // 카테고리 관련 에러
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "카테고리가 존재하지 않습니다."),
    CATEGORY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "CATEGORY_ALREADY_EXISTS", "카테고리 이름이 이미 존재합니다."),
    CATEGORY_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST,"CATEGORY_CAPACITY_EXCEEDED", "카테고리는 최대 5개 생성할 수 있습니다."),
    CATEGORY_WITH_PRODUCTS(HttpStatus.BAD_REQUEST, "CATEGORY_WITH_PRODUCTS", "상품이 있는 카테고리는 삭제할 수 없습니다."),

    // 서버 관련 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "예기치 않은 오류가 발생했습니다."),

    // 장바구니 관련 에러
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_ITEM_NOT_FOUND", "장바구니가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
