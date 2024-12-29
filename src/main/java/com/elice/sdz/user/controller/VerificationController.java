package com.elice.sdz.user.controller;

import com.elice.sdz.user.controller.apiDocs.VerificationApiDocs;
import com.elice.sdz.user.service.UserService;
import com.elice.sdz.user.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/check")
public class VerificationController implements VerificationApiDocs {

    private final UserService userService;
    private final VerificationService verificationService;

    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> checkEmailExists(@RequestBody Map<String, String> requestBody) {

        String email = requestBody.get("email");
        boolean exists = verificationService.isUserEmailExists(email);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        if (exists) {
            response.put("message", "이미 가입되어있는 이메일입니다.");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/nickname")
    public ResponseEntity<Map<String, Object>> checkNickname(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String nickname = requestBody.get("nickname");
        boolean exists = verificationService.isNicknameExists(nickname, email);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        if (exists) {
            response.put("message", "이미 사용중인 닉네임입니다.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/userLimit")
    public ResponseEntity<Map<String, Object>> checkAccountLimit(@RequestBody Map<String, String> requestBody) {

        String userName = requestBody.get("userName");
        String contact = requestBody.get("contact");

        long userLimit = userService.countEmails(userName, contact);

        Map<String, Object> response = new HashMap<>();
        response.put("userLimit", userLimit);
        if(userLimit >= 3) {
            response.put("message", "가입 가능한 계정 수가 초과되었습니다.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/userInfo")
    public ResponseEntity<Map<String, Object>> validateEmailExists(@RequestBody Map<String, String> requestBody) {

        String userName = requestBody.get("userName");
        String email = requestBody.get("email");

        boolean exists = verificationService.existsByEmailAndUserName(email, userName);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        if (!exists) {
            response.put("message", "일치하는 이메일이 존재하지 않습니다.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> checkPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String inputPassword = requestBody.get("userPassword");
        boolean valid = verificationService.checkPassword(email, inputPassword);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        if (!valid) {
            response.put("message", "패스워드가 일치하지 않습니다.");
        }
        return ResponseEntity.ok(response);
    }
}
