package com.elice.sdz.user.dto.request;

import lombok.Getter;

@Getter
public class VerificationRequestDTO {
    private String email;

    private String userPassword;

    private String userName;

    private String nickname;

    private String contact;
}
