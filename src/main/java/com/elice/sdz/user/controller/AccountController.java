package com.elice.sdz.user.controller;

import com.elice.sdz.user.controller.apiDocs.AccountApiDocs;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController implements AccountApiDocs {

    private final AccountService accountService;

    @PostMapping("/find-id")
    public ResponseEntity<List<Users>> findId(@RequestBody Map<String, String> requestBody) {
        String userName = requestBody.get("userName");
        String contact = requestBody.get("contact");

        List<Users> userIds = accountService.findByUserId(userName, contact);

        return userIds.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.emptyList()) : ResponseEntity.ok(userIds);
    }

    @PostMapping("/find-pw")
    public ResponseEntity<String> findPw(@RequestBody Map<String, String> requestBody) {
        String userName = requestBody.get("userName");
        String userId = requestBody.get("userId");
        String resultMessage = accountService.findUserPassword(userName, userId);

        return switch (resultMessage) {
            case "Temporary password issuance has been completed." ->
                    ResponseEntity.ok("Temporary password has been issued.");
            case "User not found." ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("User information not found.");
            default ->
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred on the server.");
        };
    }
}
