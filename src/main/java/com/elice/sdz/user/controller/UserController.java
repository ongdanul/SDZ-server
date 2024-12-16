package com.elice.sdz.user.controller;

import com.elice.sdz.user.dto.*;
import com.elice.sdz.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController implements UserApiDocs {

    private final UserService userService;

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String,Object>> signUpProcess(@Valid SignUpDTO signUpDTO, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", "The input values are not valid.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean isSignedUp = userService.signUpProcess(signUpDTO);
        if (isSignedUp) {
            response.put("success", true);
            response.put("message", "Sign-up successful.");
            response.put("userName", signUpDTO.getUserName());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Sign-up failed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/my-page")
    public ResponseEntity<UserDetailDTO> userDetail(){
        String userId = getCurrentUserId();
        UserDetailDTO userDetailDTO = userService.findByUserId(userId);
        return ResponseEntity.ok(userDetailDTO);
    }

    @PutMapping("/local/{userId}")
    @PreAuthorize("!@userService.isSocial(authentication.name)")
    public ResponseEntity<Map<String, Object>> updateLocalUser(@PathVariable("userId") String userId,
            @Valid @RequestBody UpdateLocalDTO updateLocalDTO, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", "The input values are not valid.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        userService.updateByLocalUser(updateLocalDTO);
        response.put("success", true);
        response.put("message", "Local user updated successfully.");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/social/{userId}")
    @PreAuthorize("@userService.isSocial(authentication.name)")
    public ResponseEntity<Map<String, Object>> updateSocialUser(@PathVariable("userId") String userId,
            @Valid @RequestBody UpdateSocialDTO updateSocialDTO, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", "The input values are not valid.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        userService.updateBySocialUser(updateSocialDTO);
        response.put("success", true);
        response.put("message", "Social user updated successfully.");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(HttpServletResponse response, @PathVariable("userId") String userId) {
        userService.deleteByUser(response, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
