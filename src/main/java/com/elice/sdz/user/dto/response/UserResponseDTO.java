package com.elice.sdz.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private boolean success;

    private String message;

    private String userName;
}
