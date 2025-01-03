package com.elice.sdz.user.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class LoginResponse {

    private HttpStatus httpStatus;

    private String errorCode;

    private String message;

}
