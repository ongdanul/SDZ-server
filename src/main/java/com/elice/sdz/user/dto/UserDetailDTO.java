package com.elice.sdz.user.dto;

import com.elice.sdz.user.entity.Users;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailDTO {

    private String email;

    private String userPassword;

    private String userName;

    private String nickname;

    private String contact;

    private boolean social;

    private String profileUrl;

    public static UserDetailDTO toDTO(Users user) {
        return UserDetailDTO.builder()
                .email(user.getEmail())
                .userPassword(user.getUserPassword())
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .contact(user.getContact())
                .social(user.isSocial())
                .profileUrl(user.getProfileUrl())
                .build();
    }
}
