package com.elice.sdz.user.service;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.PageRequestDTO;
import com.elice.sdz.user.dto.PageResponseDTO;
import com.elice.sdz.user.dto.UserListDTO;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public PageResponseDTO<UserListDTO> searchUserList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable();

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
        String keyword = pageRequestDTO.getKeyword().trim();
        String type = pageRequestDTO.getType().trim();

        if (keyword.isEmpty()) {
            return getUserListByType(type, pageable);
        }

        return getUserListByKeywordAndType(keyword, type, pageable);
    }

    private Page<Users> getUserListByType(String type, Pageable pageable) {
        if (type == null || type.isEmpty() || "all".equals(type)) {
            return userRepository.findAll(pageable);
        }

        return switch (type) {
            case "local" -> userRepository.findBySocialFalse(pageable);
            case "social" -> userRepository.findBySocialTrue(pageable);
            default -> throw new CustomException(ErrorCode.INVALID_TYPE);
        };
    }

    private Page<Users> getUserListByKeywordAndType(String keyword, String type, Pageable pageable) {
        if ("all".equals(type)) {
            return userRepository.findByEmailContaining(keyword, pageable);
        }

        if ("local".equals(type)) {
            return userRepository.findByEmailContainingAndSocialFalse(keyword, pageable);
        }

        if ("social".equals(type)) {
            return userRepository.findByEmailContainingAndSocialTrue(keyword, pageable);
        }
        throw new CustomException(ErrorCode.INVALID_TYPE);
    }

    @Transactional
    public void updateLoginLock(String userId){
        Users user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setLoginLock(!user.isLoginLock());
        userRepository.save(user);
    }

    @Transactional
    public void updateAuth(String userId){
        Users user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setUserAuth(user.getUserAuth() == Users.Auth.ROLE_USER
                ? Users.Auth.ROLE_ADMIN
                : Users.Auth.ROLE_USER);
        userRepository.save(user);
    }

    @Transactional
    public void adminDeleteUser(String userId) {
        Users user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }

    @Transactional
    public void adminDeleteByUserIds(List<String> userIds) {
        if (userIds.isEmpty()) {
            throw new CustomException(ErrorCode.NO_USER_IDS_TO_DELETE);
        }

        List<Users> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new CustomException(ErrorCode.USER_IDS_NOT_EXIST);
        }

        userRepository.deleteAllByEmailIn(userIds);
    }
}