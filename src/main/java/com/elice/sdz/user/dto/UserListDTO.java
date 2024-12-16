package com.elice.sdz.user.dto;

import com.elice.sdz.user.entity.Users;
import lombok.Data;

import java.time.Instant;

@Data
public class UserListDTO {

    private String userId;

    private Users.Auth userAuth;

    private String userName;

    private String contact;

    private Instant regDate;

    private String email;

    private boolean loginLock;

    private boolean social;

    public UserListDTO(Users user) {
        this.userId = user.getUserId();
        this.userAuth = user.getUserAuth();
        this.userName = user.getUserName();
        this.contact = user.getContact();
        this.regDate = user.getRegDate();
        this.email = user.getEmail();
        this.loginLock = user.isLoginLock();
        this.social = user.isSocial();
    }
}
