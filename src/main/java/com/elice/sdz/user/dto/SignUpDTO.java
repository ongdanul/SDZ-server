package com.elice.sdz.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpDTO {

    @NotBlank
    @Size(min = 4, max = 10)
    private String userId;

    @NotBlank
    @Size(min = 8, max = 13)
    private String userPassword;

    @NotBlank
    private String userName;

    private String nickname;

    @NotBlank
    private String contact;

    @NotBlank
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;
}
