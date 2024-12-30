package com.elice.sdz.user.entity;

import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class Users extends BaseEntity {

    @Id
    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "user_password")
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Order> orders  = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<DeliveryAddress> deliveryAddresses = new ArrayList<>();

    public enum Auth {
        ROLE_USER,
        ROLE_ADMIN;
    }
}
