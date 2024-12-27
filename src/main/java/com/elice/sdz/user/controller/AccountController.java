package com.elice.sdz.user.controller;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.controller.apiDocs.AccountApiDocs;
import com.elice.sdz.user.dto.UserAccountDTO;
import com.elice.sdz.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController implements AccountApiDocs {

    private final AccountService accountService;

    @PostMapping("/find-id")
    public ResponseEntity<List<UserAccountDTO>> findId(@RequestBody Map<String, String> requestBody) {
        String userName = requestBody.get("userName");
        String contact = requestBody.get("contact");

        List<UserAccountDTO> emails = accountService.findByEmail(userName, contact);

        if (emails.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return ResponseEntity.ok(emails);
    }

    @PostMapping("/find-pw")
    public ResponseEntity<Map<String, String>> findPw(@RequestBody Map<String, String> requestBody) {
        String userName = requestBody.get("userName");
        String email = requestBody.get("email");

        accountService.createTemporaryPassword(email, userName);

        Map<String, String> response = new HashMap<>();
        response.put("message", "임시 비밀번호가 발급되었습니다. 이메일을 확인해주세요.");
        return ResponseEntity.ok(response);
    }
}
