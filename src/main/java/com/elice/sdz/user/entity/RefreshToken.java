package com.elice.sdz.user.entity;

import com.elice.sdz.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {

    @Id
    @Column(name = "refresh_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshId;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "refresh", columnDefinition = "TEXT")
    private String refresh;

    @Column(name = "expiration")
    private String expiration;
}