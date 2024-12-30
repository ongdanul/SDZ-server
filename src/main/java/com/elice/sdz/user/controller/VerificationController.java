package com.elice.sdz.user.controller;

import com.elice.sdz.user.controller.apiDocs.VerificationApiDocs;
import com.elice.sdz.user.dto.request.VerificationRequestDTO;
import com.elice.sdz.user.dto.response.VerificationResponseDTO;
import com.elice.sdz.user.service.UserService;
import com.elice.sdz.user.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/check")
public class VerificationController implements VerificationApiDocs {

    private final UserService userService;
    private final VerificationService verificationService;

    @PostMapping("/email")
    public ResponseEntity<VerificationResponseDTO> checkEmailExists(@RequestBody VerificationRequestDTO request) {
        String email = request.getEmail();

        boolean exists = verificationService.isUserEmailExists(email);
        VerificationResponseDTO response = new VerificationResponseDTO();
        response.setExists(exists);
        if (exists) {
            response.setMessage("이미 가입되어있는 이메일입니다.");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/nickname")
    public ResponseEntity<VerificationResponseDTO> checkNickname(@RequestBody VerificationRequestDTO request) {
        String email = request.getEmail();
        String nickname = request.getNickname();

        boolean exists = verificationService.isNicknameExists(nickname, email);
        VerificationResponseDTO response = new VerificationResponseDTO();
        response.setExists(exists);
        if (exists) {
            response.setMessage("이미 사용중인 닉네임입니다.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/userLimit")
    public ResponseEntity<VerificationResponseDTO> checkAccountLimit(@RequestBody VerificationRequestDTO request) {
        String userName = request.getUserName();
        String contact = request.getContact();

        long userLimit = userService.countEmails(userName, contact);
        VerificationResponseDTO response = new VerificationResponseDTO();
        response.setUserLimit(userLimit);
        if(userLimit >= 3) {
            response.setMessage("가입 가능한 계정 수가 초과되었습니다.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/userInfo")
    public ResponseEntity<VerificationResponseDTO> validateEmailExists(@RequestBody VerificationRequestDTO request) {
        String userName = request.getUserName();
        String email = request.getEmail();

        boolean exists = verificationService.existsByEmailAndUserName(email, userName);
        VerificationResponseDTO response = new VerificationResponseDTO();
        response.setExists(exists);
        if (!exists) {
            response.setMessage("일치하는 이메일이 존재하지 않습니다.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password")
    public ResponseEntity<VerificationResponseDTO> checkPassword(@RequestBody VerificationRequestDTO request) {
        String email = request.getEmail();
        String inputPassword = request.getUserPassword();

        boolean valid = verificationService.checkPassword(email, inputPassword);
        VerificationResponseDTO response = new VerificationResponseDTO();
        response.setValid(valid);
        if (!valid) {
            response.setMessage("패스워드가 일치하지 않습니다.");
        }
        return ResponseEntity.ok(response);
    }
}
