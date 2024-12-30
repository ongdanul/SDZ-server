package com.elice.sdz.user.controller.apiDocs;

import com.elice.sdz.user.dto.request.VerificationRequestDTO;
import com.elice.sdz.user.dto.response.VerificationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface VerificationApiDocs {

    @Operation(summary = "일반 회원 회원가입시 이메일 중복 확인", description = "회원가입 시 이메일 중복 여부를 확인하는 API입니다.")
    ResponseEntity<VerificationResponseDTO> checkEmailExists(@RequestBody VerificationRequestDTO request);

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 여부를 확인하는 API입니다.")
    ResponseEntity<VerificationResponseDTO> checkNickname(@RequestBody VerificationRequestDTO request);

    @Operation(summary = "일반 회원 회원가입시 이메일 생성 제한 확인", description = "일반 회원가입시 이름과 연락처를 기준으로 조회하여 생성된 이메일이 제한 갯수를 초과하였는지 확인하는 API입니다.")
    ResponseEntity<VerificationResponseDTO> checkAccountLimit(@RequestBody VerificationRequestDTO request);

    @Operation(summary = "일반 회원 비밀번호 찾기시 이메일 존재 여부 확인", description = "일반 회원 비밀번호 찾기시 입력한 이메일과 이름으로 조회하여 일치하는 이메일이 존재하는지 확인하는 API입니다.")
    ResponseEntity<VerificationResponseDTO> validateEmailExists(@RequestBody VerificationRequestDTO request);

    @Operation(summary = "기존 비밀번호와 일치 여부 확인", description = "일반 회원의 정보 변경시 기존 비밀번호와 일치하는지 확인하는 API입니다.")
    ResponseEntity<VerificationResponseDTO> checkPassword(@RequestBody VerificationRequestDTO request);
}
