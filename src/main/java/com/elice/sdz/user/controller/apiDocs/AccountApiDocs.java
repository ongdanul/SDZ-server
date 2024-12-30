package com.elice.sdz.user.controller.apiDocs;

import com.elice.sdz.user.dto.UserAccountDTO;
import com.elice.sdz.user.dto.request.AccountRequestDTO;
import com.elice.sdz.user.dto.response.AccountResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface AccountApiDocs {

    @Operation(summary = "일반 회원 아이디 찾기", description = "일반 회원 아이디 찾기를 처리하는 API입니다.")
    ResponseEntity<List<UserAccountDTO>> findId(@RequestBody AccountRequestDTO request);

    @Operation(summary = "일반 회원 비밀번호 찾기", description = "일반 회원 비밀번호 찾기를 처리하는 API입니다.")
    ResponseEntity<AccountResponseDTO> findPw(@RequestBody AccountRequestDTO request);
}
