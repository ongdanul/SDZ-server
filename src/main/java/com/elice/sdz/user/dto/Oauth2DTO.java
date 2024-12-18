package com.elice.sdz.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Oauth2DTO {

    private String email;

    private String userName;

    private String authorities;
}
