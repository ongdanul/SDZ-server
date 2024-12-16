package com.elice.sdz.user.controller.apiDocs;

import com.elice.sdz.user.dto.PageRequestDTO;
import com.elice.sdz.user.dto.PageResponseDTO;
import com.elice.sdz.user.dto.UserListDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface AdminApiDocs {

    @Operation(summary = "회원 목록 조회 및 검색", description = "회원 목록을 조회하고 검색하는 API입니다.")
    ResponseEntity<PageResponseDTO<UserListDTO>> userList(@RequestParam PageRequestDTO pageRequestDTO);
}
