package com.elice.sdz.user.controller;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.controller.apiDocs.AccountApiDocs;
import com.elice.sdz.user.dto.UserAccountDTO;
import com.elice.sdz.user.dto.request.AccountRequestDTO;
import com.elice.sdz.user.dto.response.AccountResponseDTO;
import com.elice.sdz.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController implements AccountApiDocs {

    private final AccountService accountService;

    @PostMapping("/find-id")
    public ResponseEntity<AccountResponseDTO> findId(@RequestBody AccountRequestDTO request) {
        String userName = request.getUserName();
        String contact = request.getContact();

        List<UserAccountDTO> emails = accountService.findByEmail(userName, contact);
        AccountResponseDTO response = new AccountResponseDTO();
        response.setEmails(emails);
        if (emails.isEmpty()) {
            response.setMessage("일치하는 회원정보가 존재하지않습니다.");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/find-pw")
    public ResponseEntity<AccountResponseDTO> findPw(@RequestBody AccountRequestDTO request) {
        String userName = request.getUserName();
        String email = request.getEmail();

        accountService.createTemporaryPassword(email, userName);
        AccountResponseDTO response = new AccountResponseDTO();
        response.setMessage("임시 비밀번호가 발급되었습니다. 이메일을 확인해주세요.");
        return ResponseEntity.ok(response);
    }
}
