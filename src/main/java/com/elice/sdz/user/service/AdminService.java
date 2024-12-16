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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public PageResponseDTO<UserListDTO> searchUserList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable();

        Page<Users> result;

        String keyword = pageRequestDTO.getKeyword();
        String type = pageRequestDTO.getType();

        if (type == null || type.isEmpty() || keyword == null || keyword.isEmpty()) {
            result = userRepository.findAll(pageable);
        } else {
            switch (type) {
                case "local" ->
                    result = userRepository.findByUserIdContainingAndSocialFalse(keyword, pageable);
                case "social" ->
                    result = userRepository.findByUserIdContainingAndSocialTrue(keyword, pageable);
                default ->
                    result = userRepository.findAll(pageable);
            }
        }

        List<Users> usersList = result.getContent();

        List<UserListDTO> dtoList = usersList.stream()
                .map(UserListDTO::new)
                .collect(Collectors.toList());

        return PageResponseDTO.from(pageRequestDTO, dtoList, (int) result.getTotalElements(), keyword);
    }
}