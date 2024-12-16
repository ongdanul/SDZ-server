package com.elice.sdz.user.repository;

import com.elice.sdz.user.entity.SocialUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialRepository extends JpaRepository<SocialUsers, Long> {

    Optional<SocialUsers> findByProviderAndProviderId(String provider, String providerId);
}
