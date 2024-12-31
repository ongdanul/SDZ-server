package com.elice.sdz.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationResponseDTO {

    private boolean exists;

    private boolean valid;

    private Long userLimit;

    private String message;
}
