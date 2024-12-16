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

    @PostMapping("/userId")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@RequestBody Map<String, String> requestBody) {

        String userId = requestBody.get("userId");
        boolean exists = verificationService.isUserIdExists(userId);

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
    public ResponseEntity<Map<String, Boolean>> checkUserLimit(@RequestBody Map<String, String> requestBody) {

        String userName = requestBody.get("userName");
        String contact = requestBody.get("contact");

        long userLimit = userService.countUserIds(userName, contact);

        Map<String, Boolean> response = new HashMap<>();
        response.put("userLimit", userLimit < 3);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/userInfo")
    public ResponseEntity<Map<String, Boolean>> checkUser(@RequestBody Map<String, String> requestBody) {

        String userName = requestBody.get("userName");
        String userId = requestBody.get("userId");

        boolean exists = verificationService.existsByUserIdAndUserName(userName, userId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, Boolean>> checkPassword(@RequestBody Map<String, String> requestBody) {

        String userId = requestBody.get("userId");
        String inputPassword = requestBody.get("userPassword");
        boolean valid = verificationService.checkPassword(userId, inputPassword);

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);

        return ResponseEntity.ok(response);
    }
}
