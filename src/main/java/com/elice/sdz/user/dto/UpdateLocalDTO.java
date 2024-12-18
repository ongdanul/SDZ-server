package com.elice.sdz.user.dto;

import com.elice.sdz.user.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateLocalDTO {
    @NotBlank
    private String userId;

    @NotBlank
    @Size(min = 8, max = 13)
    private String userPassword;

    @NotBlank
    private String userName;

    private String nickname;

    @NotBlank
    private String contact;

    @Email
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    private boolean social;

    public void updateEntity(Users user, String encodedPassword) {
        user.setUserPassword(encodedPassword);
        user.setUserName(userName);
        user.setNickname(nickname == null ? userName : nickname);
        user.setContact(contact);
        user.setEmail(email);
    }
}
