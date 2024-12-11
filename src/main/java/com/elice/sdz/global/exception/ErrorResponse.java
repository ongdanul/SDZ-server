package com.elice.sdz.global.exception;

import ch.qos.logback.core.spi.ErrorCodes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private HttpStatus status;
    private String errorCode;
    private String message;

    public static ErrorResponse of(ErrorCode errorCode) {
       return new ErrorResponse(
               errorCode.getHttpStatus(),
               errorCode.getErrorCode(),
               errorCode.getMessage()
               );
    }

}
