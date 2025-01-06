package com.elice.sdz.user.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RefreshResponse {

    private HttpStatus httpStatus;
    private String message;

}
