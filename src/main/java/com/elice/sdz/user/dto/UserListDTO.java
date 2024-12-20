package com.elice.sdz.user.dto;

import com.elice.sdz.user.entity.Users;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class UserListDTO {

    private String email;

    private Users.Auth userAuth;

    private String userName;

    private String contact;

    private LocalDateTime createdAt;

    private boolean loginLock;

    private boolean social;

    public UserListDTO(Users user) {
        this.email = user.getEmail();
        this.userAuth = user.getUserAuth();
        this.userName = user.getUserName();
        this.contact = user.getContact();
        this.createdAt = user.getCreatedAt();
        this.loginLock = user.isLoginLock();
        this.social = user.isSocial();
    }
}
