package com.elice.sdz.user.controller;

import com.elice.sdz.user.controller.apiDocs.AdminApiDocs;
import com.elice.sdz.user.dto.PageRequestDTO;
import com.elice.sdz.user.dto.PageResponseDTO;
import com.elice.sdz.user.dto.UserListDTO;
import com.elice.sdz.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController implements AdminApiDocs {

    private final AdminService adminService;

    @GetMapping("/user-management")
    public ResponseEntity<PageResponseDTO<UserListDTO>> userList(@RequestParam PageRequestDTO pageRequestDTO){
        PageResponseDTO<UserListDTO> response = adminService.searchUserList(pageRequestDTO);

        return ResponseEntity.ok(response);
    }

}