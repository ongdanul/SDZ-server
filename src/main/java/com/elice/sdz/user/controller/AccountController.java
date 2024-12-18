package com.elice.sdz.user.controller;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.controller.apiDocs.AccountApiDocs;
import com.elice.sdz.user.dto.UserIdsDTO;
import com.elice.sdz.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController implements AccountApiDocs {

    private final AccountService accountService;

    @PostMapping("/find-id")
    public ResponseEntity<List<UserIdsDTO>> findId(@RequestBody Map<String, String> requestBody) {
        String userName = requestBody.get("userName");
        String contact = requestBody.get("contact");

        List<UserIdsDTO> userIds = accountService.findByUserId(userName, contact);

        if (userIds.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return ResponseEntity.ok(userIds);
    }

    @PostMapping("/find-pw")
    public ResponseEntity<String> findPw(@RequestBody Map<String, String> requestBody) {
        String userName = requestBody.get("userName");
        String userId = requestBody.get("userId");

        accountService.createTemporaryPassword(userId, userName);
        return ResponseEntity.ok("임시 비밀번호가 발급되었습니다.");
    }
}
