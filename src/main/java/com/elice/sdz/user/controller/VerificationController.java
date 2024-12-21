package com.elice.sdz.user.controller;

import com.elice.sdz.user.controller.apiDocs.VerificationApiDocs;
import com.elice.sdz.user.service.UserService;
import com.elice.sdz.user.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/check")
public class VerificationController implements VerificationApiDocs {

    private final UserService userService;
    private final VerificationService verificationService;

    @PostMapping("/email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestBody Map<String, String> requestBody) {

        String email = requestBody.get("email");
        boolean exists = verificationService.isUserEmailExists(email);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestBody Map<String, String> requestBody) {

        String nickname = requestBody.get("nickname");
        boolean exists = verificationService.isNicknameExists(nickname);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/userLimit")
    public ResponseEntity<Map<String, Boolean>> checkAccountLimit(@RequestBody Map<String, String> requestBody) {

        String userName = requestBody.get("userName");
        String contact = requestBody.get("contact");

        long userLimit = userService.countEmails(userName, contact);

        Map<String, Boolean> response = new HashMap<>();
        response.put("userLimit", userLimit < 3);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/userInfo")
    public ResponseEntity<Map<String, Boolean>> validateEmailExists(@RequestBody Map<String, String> requestBody) {

        String userName = requestBody.get("userName");
        String email = requestBody.get("email");

        boolean exists = verificationService.existsByEmailAndUserName(userName, email);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, Boolean>> checkPassword(@RequestBody Map<String, String> requestBody) {

        String email = requestBody.get("email");
        String inputPassword = requestBody.get("userPassword");
        boolean valid = verificationService.checkPassword(email, inputPassword);

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);

        return ResponseEntity.ok(response);
    }
}
