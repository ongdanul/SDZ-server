package com.elice.sdz.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "social_users")
public class SocialUsers {

    @Id
    @Column(name = "social_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long socialId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

    @Column(name = "social_provider", length = 50, nullable = false)
    private String socialProvider;

    @Column(name = "social_provider_id", length = 50, nullable = false)
    private String socialProviderId;
}
