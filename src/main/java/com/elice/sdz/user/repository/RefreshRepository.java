package com.elice.sdz.user.repository;

import com.elice.sdz.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByRefresh(String refresh);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.refresh = :newRefresh, r.expiration = :expiration WHERE r.email = :email")
    int updateRefreshToken(@Param("email") String email, @Param("newRefresh") String newRefresh, @Param("expiration") String expiration);

    @Transactional
    void deleteByRefresh(String refresh);

    @Transactional
    void deleteAllByEmail(String email);
}
