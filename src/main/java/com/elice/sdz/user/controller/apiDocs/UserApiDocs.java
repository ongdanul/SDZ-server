package com.elice.sdz.user.controller.apiDocs;

import com.elice.sdz.user.dto.SignUpDTO;
import com.elice.sdz.user.dto.UpdateLocalDTO;
import com.elice.sdz.user.dto.UpdateSocialDTO;
import com.elice.sdz.user.dto.UserDetailDTO;
import com.elice.sdz.user.dto.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface UserApiDocs {

    @Operation(summary = "일반 회원 회원가입", description = "일반 회원 회원가입을 처리하는 API입니다.")
    ResponseEntity<UserResponseDTO> signUpProcess(SignUpDTO signUpDTO, BindingResult bindingResult);

    @Operation(summary = "회원 정보 조회", description = "회원 정보 조회를 처리하는 API입니다.")
    ResponseEntity<UserDetailDTO> userDetail();

    @Operation(summary = "일반 회원 정보 수정", description = "일반 회원 정보 수정을 처리하는 API입니다.")
    ResponseEntity<UserResponseDTO> updateLocalUser(@PathVariable("email") String email, @Valid @RequestBody UpdateLocalDTO updateLocalDTO, BindingResult bindingResult);

    @Operation(summary = "소셜 회원 정보 수정", description = "소셜 회원 정보 수정을 처리하는 API입니다.")
    ResponseEntity<UserResponseDTO> updateSocialUser(@PathVariable("email") String email, @Valid @RequestBody UpdateSocialDTO updateSocialDTO, BindingResult bindingResult);

    @Operation(summary = "회원 삭제", description = "회원 삭제를 처리하는 API입니다.")
    ResponseEntity<Void> deleteUser(HttpServletResponse response, @PathVariable("email") String email);
}
