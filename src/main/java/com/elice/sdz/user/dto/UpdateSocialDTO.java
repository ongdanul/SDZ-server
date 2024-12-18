package com.elice.sdz.user.dto;

import com.elice.sdz.user.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateSocialDTO {
    @NotBlank
    private String userId;

    @NotBlank
    private String userName;

    private String nickname;

    @NotBlank
    private String contact;

    @NotBlank
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    private boolean social;

    public void updateEntity(Users user) {
        user.setUserName(userName);
        user.setNickname(nickname == null ? userName : nickname);
        user.setContact(contact);
        user.setEmail(email);
    }
}
