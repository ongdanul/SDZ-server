package com.elice.sdz.user.entity;

import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.review.entity.Review;
import com.elice.sdz.user.dto.SignUpDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class Users {

    @Id
    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "user_password", length = 50)
    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_auth", nullable = false)
    private Auth userAuth;

    @Column(name = "user_name", length = 50)
    private String userName;

    @Column(name = "nickname", length = 20)
    private String nickname;

    @Column(name = "contact", length = 20)
    private String contact;

    @Column(name = "reg_date", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant regDate;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "login_lock", nullable = false,
            columnDefinition = "BIT(1) DEFAULT 0")
    private boolean loginLock;

    @Column(name = "login_attempts", nullable = false,
            columnDefinition = "INT DEFAULT 0")
    private int loginAttempts;

    @Column(name = "last_failed_login",
            columnDefinition = "TIMESTAMP DEFAULT NULL")
    private Instant lastFailedLogin;

    @Column(name = "social", nullable = false,
            columnDefinition = "BIT(1) DEFAULT 0")
    private boolean social;

    @Column(name = "profile_url")
    private String profileUrl;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Order> orders  = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<DeliveryAddress> deliveryAddresses = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Review> reviews = new ArrayList<>();

    public enum Auth {
        ROLE_USER,
        ROLE_ADMIN;
    }

    public Users signUpToEntity (SignUpDTO dto) {
        return Users.builder()
                    .userId(dto.getUserId())
                    .userPassword(dto.getUserPassword())
                    .userAuth(Auth.ROLE_USER)
                    .userName(dto.getUserName())
                    .nickname(Optional.ofNullable(dto.getNickname()).orElse(dto.getUserName()))
                    .contact(dto.getContact())
                    .email(dto.getEmail())
                    .build();
    }
}
