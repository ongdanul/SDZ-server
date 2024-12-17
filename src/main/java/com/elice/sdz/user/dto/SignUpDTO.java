package com.elice.sdz.user.dto;

import com.elice.sdz.user.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Optional;

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

    public Users toEntity () {
        return Users.builder()
                .userId(userId)
                .userPassword(userPassword)
                .userAuth(Users.Auth.ROLE_USER)
                .userName(userName)
                .nickname(nickname == null ? userName : nickname)
                .contact(contact)
                .email(email)
                .build();
    }
}
