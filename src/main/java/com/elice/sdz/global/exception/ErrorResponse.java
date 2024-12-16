package com.elice.sdz.global.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ErrorResponse {

    private HttpStatus status;
    private String errorCode;
    private String message;

    public static ErrorResponse of(ErrorCode errorCode) {
       return ErrorResponse.builder()
               .status(errorCode.getHttpStatus())
               .errorCode(errorCode.getErrorCode())
               .message(errorCode.getMessage())
               .build();
    }

}
