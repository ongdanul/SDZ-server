package com.elice.sdz.user.repository;

import com.elice.sdz.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {

    Optional<Users> findByUserId(String userId);
    long countByUserNameAndContact(String userName, String contact);

    //Account
    List<Users> findByLastFailedLoginIsNotNull();
    List<Users> findByUserNameAndContactAndSocialFalse(String userName, String contact);
    Optional<Users> findByUserIdAndUserName(String userId, String userName);

    //Verification
    @Query("SELECT u.userPassword FROM Users u WHERE u.userId = :userId")
    String findPasswordByUserId(@Param("userId") String userId);
    boolean existsByUserId(String userId);
    boolean existsByNickname(String nickname);
    boolean existsByUserIdAndUserName(String userId, String userName);

    //Admin
    Page<Users> findByUserIdContainingAndSocialTrue(String userId, Pageable pageable);
    Page<Users> findByUserIdContainingAndSocialFalse(String userId, Pageable pageable);

    String findEmailByUserId(String userId);
}
