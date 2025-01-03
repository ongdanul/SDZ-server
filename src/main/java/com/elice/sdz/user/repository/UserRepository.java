package com.elice.sdz.user.repository;

import com.elice.sdz.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    long countByUserNameAndContact(String userName, String contact);

    //Account
    List<Users> findByLastFailedLoginIsNotNull();
    List<Users> findByUserNameAndContactAndSocialFalse(String userName, String contact);
    Optional<Users> findByEmailAndUserName(String email, String userName);

    //Verification
    @Query("SELECT u.userPassword FROM Users u WHERE u.email = :email")
    String findPasswordByEmail(@Param("email") String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmailAndUserName(String email, String userName);

    //Admin
    Page<Users> findAllByDeactivatedFalse(Pageable pageable);
    Page<Users> findBySocialTrueAndDeactivatedFalse(Pageable pageable);
    Page<Users> findBySocialFalseAndDeactivatedFalse(Pageable pageable);
    Page<Users> findByEmailContainingAndDeactivatedFalse(String email, Pageable pageable);
    Page<Users> findByEmailContainingAndSocialTrueAndDeactivatedFalse(String email, Pageable pageable);
    Page<Users> findByEmailContainingAndSocialFalseAndDeactivatedFalse(String email, Pageable pageable);
    @Query("SELECT COUNT(u) FROM Users u WHERE u.userAuth = 'ROLE_ADMIN'")
    long countByRoleAdmin();

    //Scheduler
    List<Users> findByDeactivatedTrueAndDeactivationTimeBefore(Instant deactivationTime);
}
