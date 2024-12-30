package com.elice.sdz.user.entity;

import com.elice.sdz.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "social_users")
public class SocialUsers extends BaseEntity {

    @Id
    @Column(name = "social_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long socialId;

    @OneToOne
    @JoinColumn(name = "email", nullable = false)
    private Users user;

    @Column(name = "social_provider", length = 50, nullable = false)
    private String socialProvider;

    @Column(name = "social_provider_id", length = 50, nullable = false)
    private String socialProviderId;
}
