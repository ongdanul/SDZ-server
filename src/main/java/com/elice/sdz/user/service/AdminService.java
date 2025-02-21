package com.elice.sdz.user.service;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.request.PageRequestDTO;
import com.elice.sdz.user.dto.response.PageResponseDTO;
import com.elice.sdz.user.dto.UserListDTO;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.RefreshRepository;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;

    public PageResponseDTO<UserListDTO> searchUserList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("createdAt");

        Page<Users> result = searchWithFilters(pageRequestDTO, pageable);

        List<UserListDTO> dtoList = result.getContent()
                .stream()
                .map(UserListDTO::new)
                .collect(Collectors.toList());

        return PageResponseDTO.<UserListDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .keyword(pageRequestDTO.getKeyword())
                .build();
    }

    private Page<Users> searchWithFilters(PageRequestDTO pageRequestDTO, Pageable pageable) {
        String keyword = pageRequestDTO.getKeyword() != null ? pageRequestDTO.getKeyword().trim() : "";
        String type = pageRequestDTO.getType() != null ? pageRequestDTO.getType().trim() : "all";

        if (keyword.isEmpty()) {
            return getUserListByType(type, pageable);
        }

        return getUserListByKeywordAndType(keyword, type, pageable);
    }

    private Page<Users> getUserListByType(String type, Pageable pageable) {
        if (type == null || type.isEmpty() || "all".equals(type)) {
            return userRepository.findAllByDeactivatedFalse(pageable);
        }

        return switch (type) {
            case "local" -> userRepository.findBySocialFalseAndDeactivatedFalse(pageable);
            case "social" -> userRepository.findBySocialTrueAndDeactivatedFalse(pageable);
            default -> throw new CustomException(ErrorCode.INVALID_TYPE);
        };
    }

    private Page<Users> getUserListByKeywordAndType(String keyword, String type, Pageable pageable) {
        if ("all".equals(type)) {
            return userRepository.findByEmailContainingAndDeactivatedFalse(keyword, pageable);
        }

        if ("local".equals(type)) {
            return userRepository.findByEmailContainingAndSocialFalseAndDeactivatedFalse(keyword, pageable);
        }

        if ("social".equals(type)) {
            return userRepository.findByEmailContainingAndSocialTrueAndDeactivatedFalse(keyword, pageable);
        }
        throw new CustomException(ErrorCode.INVALID_TYPE);
    }

    @Transactional
    public void updateLoginLock(String email){
        Users user = userRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setLoginLock(!user.isLoginLock());
        userRepository.save(user);
    }

    @Transactional
    public void updateAuth(String email){
        Users user = userRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setUserAuth(user.getUserAuth() == Users.Auth.ROLE_USER
                ? Users.Auth.ROLE_ADMIN
                : Users.Auth.ROLE_USER);
        userRepository.save(user);
    }

    @Transactional
    public void adminDeleteUser(String email) {
        Users user = userRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(user.getUserAuth().equals(Users.Auth.ROLE_ADMIN) && userRepository.countByRoleAdmin()<=1) {
            throw new CustomException(ErrorCode.ADMIN_USER_EXISTS);
        }

        user.setDeactivated(true);
        user.setDeactivationTime(Instant.now());
        userRepository.save(user);

        try {
            refreshRepository.deleteAllByEmail(email);
        } catch (Exception e) {
            log.error("회원 {} 에 대한 리프레시 토큰 삭제 중 오류가 발생했습니다.", email, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void adminDeleteByUsers(List<String> emails) {
        if (emails.isEmpty()) {
            throw new CustomException(ErrorCode.NO_USER_IDS_TO_DELETE);
        }

        List<Users> users = userRepository.findAllById(emails);
        if (users.size() != emails.size()) {
            throw new CustomException(ErrorCode.USER_IDS_NOT_EXIST);
        }

        long adminCountInDeleteList = users.stream()
                .filter(user -> user.getUserAuth().equals(Users.Auth.ROLE_ADMIN))
                .count();

        long totalAdminCount = userRepository.countByRoleAdmin();

        if (adminCountInDeleteList > 0 && totalAdminCount - adminCountInDeleteList <= 1) {
            throw new CustomException(ErrorCode.ADMIN_USER_EXISTS);
        }

        for (Users user : users) {
            user.setDeactivated(true);
            user.setDeactivationTime(Instant.now());
            userRepository.save(user);
        }

        for (String email : emails) {
            try {
                refreshRepository.deleteAllByEmail(email);
            } catch (Exception e) {
                log.error("회원 {} 에 대한 리프레시 토큰 삭제 중 오류가 발생했습니다.", email, e);
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
    }
}