package com.elice.sdz.user.dto;

import lombok.Data;

@Data
public class LoginRequest {

        private String email;

        private String password;

        private boolean rememberId;

        private boolean rememberMe;
}
