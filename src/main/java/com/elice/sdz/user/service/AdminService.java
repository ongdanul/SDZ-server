package com.elice.sdz.user.service;

import com.elice.sdz.user.dto.PageRequestDTO;
import com.elice.sdz.user.dto.PageResponseDTO;
import com.elice.sdz.user.dto.UserListDTO;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

        if (keyword == null || keyword.isEmpty()) {
            return getUserListByType(type, pageable);
        }

        return getUserListByKeywordAndType(keyword, type, pageable);
    }

    private Page<Users> getUserListByType(String type, Pageable pageable) {
        if (type == null || type.isEmpty() || "all".equals(type)) {
            return userRepository.findAll(pageable);
        }

        switch (type) {
            case "local":
                return userRepository.findBySocialFalse(pageable);
            case "social":
                return userRepository.findBySocialTrue(pageable);
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    private Page<Users> getUserListByKeywordAndType(String keyword, String type, Pageable pageable) {
        if ("all".equals(type)) {
            return userRepository.findByUserIdContaining(keyword, pageable);
        }

        if ("local".equals(type)) {
            return userRepository.findByUserIdContainingAndSocialFalse(keyword, pageable);
        }

        if ("social".equals(type)) {
            return userRepository.findByUserIdContainingAndSocialTrue(keyword, pageable);
        }

        throw new IllegalArgumentException("Invalid type: " + type);
    }

    @Transactional
    public void updateLoginLock(String userId){
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setLoginLock(!user.isLoginLock());
        userRepository.save(user);
    }

    @Transactional
    public void updateAuth(String userId){
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean userAuth = user.getUserAuth().name().equals("ROLE_USER");
        if(userAuth) {
            user.setUserAuth(Users.Auth.ROLE_ADMIN);
        } else  {
            user.setUserAuth(Users.Auth.ROLE_USER);
        }
        userRepository.save(user);
    }

    @Transactional
    public void adminDeleteUser(String userId) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.delete(user);
    }

    @Transactional
    public void adminDeleteByUserIds(List<String> userIds) {
        if (userIds.isEmpty()) {
            throw new IllegalArgumentException("No user IDs to delete.");
        }

        List<Users> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new IllegalArgumentException("user IDs do not exist.");
        }

        userRepository.deleteAllByUserIdIn(userIds);
    }
}