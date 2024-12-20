package com.elice.sdz.user.controller;

import com.elice.sdz.user.controller.apiDocs.AdminApiDocs;
import com.elice.sdz.user.dto.PageRequestDTO;
import com.elice.sdz.user.dto.PageResponseDTO;
import com.elice.sdz.user.dto.UserListDTO;
import com.elice.sdz.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController implements AdminApiDocs {

    private final AdminService adminService;

    @GetMapping("/user-management")
    public ResponseEntity<PageResponseDTO<UserListDTO>> userList(@ParameterObject PageRequestDTO pageRequestDTO) {
        PageResponseDTO<UserListDTO> response = adminService.searchUserList(pageRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{email}/login-lock")
    public ResponseEntity<String> updateLoginLock(@PathVariable("email") String email) {
        adminService.updateLoginLock(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{email}/auth")
    public ResponseEntity<String> updateAuth(@PathVariable("email") String email) {
        adminService.updateAuth(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable("email") String email) {
        adminService.adminDeleteUser(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUsers(@RequestBody List<String> emails) {
        adminService.adminDeleteByUsers(emails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}