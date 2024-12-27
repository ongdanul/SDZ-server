package com.elice.sdz.user.controller.apiDocs;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface VerificationApiDocs {

    @Operation(summary = "일반 회원 회원가입시 이메일 중복 확인", description = "회원가입 시 이메일 중복 여부를 확인하는 API입니다.")
    ResponseEntity<Map<String, Object>> checkEmailExists(@RequestBody Map<String, String> requestBody);

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 여부를 확인하는 API입니다.")
    ResponseEntity<Map<String, Object>> checkNickname(@RequestBody Map<String, String> requestBody);

    @Operation(summary = "일반 회원 회원가입시 이메일 생성 제한 확인", description = "일반 회원가입시 이름과 연락처를 기준으로 조회하여 생성된 이메일이 제한 갯수를 초과하였는지 확인하는 API입니다.")
    ResponseEntity<Map<String, Object>> checkAccountLimit(@RequestBody Map<String, String> requestBody);

    @Operation(summary = "일반 회원 비밀번호 찾기시 이메일 존재 여부 확인", description = "일반 회원 비밀번호 찾기시 입력한 이메일과 이름으로 조회하여 일치하는 이메일이 존재하는지 확인하는 API입니다.")
    ResponseEntity<Map<String, Object>> validateEmailExists(@RequestBody Map<String, String> requestBody);

    @Operation(summary = "기존 비밀번호와 일치 여부 확인", description = "일반 회원의 정보 변경시 기존 비밀번호와 일치하는지 확인하는 API입니다.")
    ResponseEntity<Map<String, Object>> checkPassword(@RequestBody Map<String, String> requestBody);
}
