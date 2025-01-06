package com.elice.sdz.user.controller.apiDocs;

import com.elice.sdz.user.dto.request.PageRequestDTO;
import com.elice.sdz.user.dto.response.PageResponseDTO;
import com.elice.sdz.user.dto.UserListDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AdminApiDocs {


    @Operation(summary = "회원 목록 조회 및 검색", description = "회원 목록을 조회하고 검색하는 API입니다.")
    ResponseEntity<PageResponseDTO<UserListDTO>> userList(@ParameterObject PageRequestDTO pageRequestDTO);

    @Operation(summary = "회원 로그인 잠금 여부 변경", description = "회원의 로그인 잠금 여부를 변경하는 API입니다.")
    ResponseEntity<String> updateLoginLock(@PathVariable("email") String email);

    @Operation(summary = "회원 권한 변경", description = "회원의 권한을 변경하는 API입니다.")
    ResponseEntity<String> updateAuth(@PathVariable("email") String email);

    @Operation(summary = "회원 삭제", description = "회원 삭제를 처리하는 API입니다.")
    ResponseEntity<Void> deleteUser(@PathVariable("email") String email);

    @Operation(summary = "회원 다중 삭제", description = "회원을 체크박스를 통해 다중 삭제 처리하는 API입니다.")
    ResponseEntity<Void> deleteUsers(@RequestBody List<String> emails);
}
