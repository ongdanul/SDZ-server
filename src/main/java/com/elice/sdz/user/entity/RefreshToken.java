package com.elice.sdz.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @Column(name = "refresh_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "refresh", columnDefinition = "TEXT")
    private String refresh;

    @Column(name = "expiration")
    private String expiration;
}