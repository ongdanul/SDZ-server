package com.elice.sdz.user.controller;

import com.elice.sdz.user.controller.apiDocs.UserApiDocs;
import com.elice.sdz.user.dto.*;
import com.elice.sdz.user.dto.response.UserResponseDTO;
import com.elice.sdz.user.dto.response.VerificationResponseDTO;
import com.elice.sdz.user.service.AuthenticationService;
import com.elice.sdz.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController implements UserApiDocs {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Override
    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> signUpProcess(@RequestBody @Valid SignUpDTO signUpDTO, BindingResult bindingResult) {
        UserResponseDTO response = new UserResponseDTO();
        if (bindingResult.hasErrors()) {
            response.setSuccess(false);
            response.setMessage("입력 값이 유효하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean isSignedUp = userService.signUpProcess(signUpDTO);
        if (isSignedUp) {
            response.setSuccess(true);
            response.setMessage("회원가입이 성공적으로 완료되었습니다.");
            response.setUserName(signUpDTO.getUserName());
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            response.setMessage("회원가입에 실패했습니다. 다시 시도해 주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/my-page")
    public ResponseEntity<UserDetailDTO> userDetail(){
        String email = authenticationService.getCurrentUser();
        UserDetailDTO userDetailDTO = userService.findUserInfo(email);
        return ResponseEntity.ok(userDetailDTO);
    }

    @PutMapping("/local/{email}")
    public ResponseEntity<UserResponseDTO> updateLocalUser(@PathVariable("email") String email,
            @Valid @RequestBody UpdateLocalDTO updateLocalDTO, BindingResult bindingResult) {
        UserResponseDTO response = new UserResponseDTO();
        if (bindingResult.hasErrors()) {
            response.setSuccess(false);
            response.setMessage("입력 값이 유효하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        updateLocalDTO.setEmail(email);
        userService.updateLocalUser(updateLocalDTO);
        response.setSuccess(true);
        response.setMessage("회원 정보가 성공적으로 변경되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/social/{email}")
    public ResponseEntity<UserResponseDTO> updateSocialUser(@PathVariable("email") String email,
            @Valid @RequestBody UpdateSocialDTO updateSocialDTO, BindingResult bindingResult) {
        UserResponseDTO response = new UserResponseDTO();
        if (bindingResult.hasErrors()) {
            response.setSuccess(false);
            response.setMessage("입력 값이 유효하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        updateSocialDTO.setEmail(email);
        userService.updateSocialUser(updateSocialDTO);
        response.setSuccess(true);
        response.setMessage("회원 정보가 성공적으로 변경되었습니다.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(HttpServletResponse response, @PathVariable("email") String email) {
        userService.deleteUser(response, email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
