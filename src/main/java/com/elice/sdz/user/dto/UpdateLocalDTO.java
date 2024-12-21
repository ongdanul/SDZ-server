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
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @NotBlank
    @Size(min = 8, max = 13)
    private String userPassword;

    @NotBlank
    private String userName;

    @NotBlank
    private String nickname;

    @NotBlank
    private String contact;

    private boolean social;

    public void updateEntity(Users user, String encodedPassword) {
        user.setEmail(email);
        user.setUserPassword(encodedPassword);
        user.setUserName(userName);
        user.setNickname(nickname);
        user.setContact(contact);
    }
}
