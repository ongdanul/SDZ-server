package com.elice.sdz.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailDTO {

    private String userId;

    private String userPassword;

    private String userName;

    private String nickname;

    private String contact;

    private String email;

    private boolean social;

    private String profileUrl;
}
