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
    long countByUserNameAndContact(String userName, String contact);

    //Account
    List<Users> findByLastFailedLoginIsNotNull();
    List<Users> findByUserNameAndContactAndSocialFalse(String userName, String contact);
    Optional<Users> findByEmailAndUserName(String email, String userName);

    //Verification
//    @Query("SELECT u.userPassword FROM Users u WHERE u.email = :email")
    String findPasswordByEmail(/*@Param("email")*/ String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmailAndUserName(String email, String userName);

    //Admin
    Page<Users> findBySocialTrue(Pageable pageable);
    Page<Users> findBySocialFalse(Pageable pageable);
    Page<Users> findByEmailContaining(String email, Pageable pageable);
    Page<Users> findByEmailContainingAndSocialTrue(String email, Pageable pageable);
    Page<Users> findByEmailContainingAndSocialFalse(String email, Pageable pageable);
    void deleteAllByEmailIn(List<String> emails);
}
