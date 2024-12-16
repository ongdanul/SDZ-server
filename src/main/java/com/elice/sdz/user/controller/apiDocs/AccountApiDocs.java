package com.elice.sdz.user.controller.apiDocs;

import com.elice.sdz.user.entity.Users;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface AccountApiDocs {

    @Operation(summary = "일반 회원 아이디 찾기", description = "일반 회원 아이디 찾기를 처리하는 API입니다.")
    ResponseEntity<List<Users>> findId(@RequestBody Map<String, String> requestBody);

    @Operation(summary = "일반 회원 비밀번호 찾기", description = "일반 회원 비밀번호 찾기를 처리하는 API입니다.")
    ResponseEntity<String> findPw(@RequestBody Map<String, String> requestBody);
}
