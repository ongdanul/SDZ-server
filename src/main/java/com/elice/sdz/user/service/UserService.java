    package com.elice.sdz.user.service;

    import com.elice.sdz.global.config.CookieUtils;
    import com.elice.sdz.global.exception.CustomException;
    import com.elice.sdz.global.exception.ErrorCode;
    import com.elice.sdz.user.dto.UpdateLocalDTO;
    import com.elice.sdz.user.dto.UserDetailDTO;
    import com.elice.sdz.user.dto.SignUpDTO;
    import com.elice.sdz.user.dto.UpdateSocialDTO;
    import com.elice.sdz.user.entity.Users;
    import com.elice.sdz.user.repository.RefreshRepository;
    import com.elice.sdz.user.repository.UserRepository;
    import jakarta.servlet.http.HttpServletResponse;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.util.StringUtils;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class UserService {

        private final RefreshRepository refreshRepository;
        private final UserRepository userRepository;
        private final BCryptPasswordEncoder bCryptPasswordEncoder;

        @Transactional
        public boolean signUpProcess(@Valid SignUpDTO signUpDTO) {
            final int MAX_USER_ACCOUNTS = 3;

            long countUserIds = countUserIds(signUpDTO.getUserName(), signUpDTO.getContact());
            if (countUserIds >= MAX_USER_ACCOUNTS) { //최대 가입 계정 수 3개 제한
                log.error("최대 가입 계정 수가 초과되었습니다. - 가입 시도한 회원 정보: userName={} contact={}", signUpDTO.getUserName(), signUpDTO.getContact());
                throw new CustomException(ErrorCode.SIGN_UP_LIMIT_EXCEEDED);
            }

            signUpDTO.setUserPassword(bCryptPasswordEncoder.encode(signUpDTO.getUserPassword()));
            Users user = signUpDTO.toEntity();

            try {
                userRepository.save(user);
                log.info("회원가입에 성공하였습니다.: {}", user.getUserId());
                return true;
            } catch (Exception e) {
                log.error("회원가입 처리 중 오류가 발생하였습니다.: {}", e.getMessage(), e);
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        public long countUserIds(String userName, String contact) {
            try {
                return userRepository.countByUserNameAndContact(userName, contact);
            } catch (Exception e) {
                log.error("회원 ID를 계산하는 중 오류가 발생하였습니다.", e);
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        public UserDetailDTO findByUserId(String userId) {
            Users user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            return UserDetailDTO.toDTO(user);
        }

        @Transactional
        public void updateLocalUser(UpdateLocalDTO updateLocalDTO) {
            Users user = userRepository.findByUserId(updateLocalDTO.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            String encodedPassword = updateLocalDTO.getUserPassword() != null ?
                    bCryptPasswordEncoder.encode(updateLocalDTO.getUserPassword()) : null;

            updateLocalDTO.updateEntity(user, encodedPassword);

            userRepository.save(user);
        }

        @Transactional
        public void updateSocialUser(UpdateSocialDTO updateSocialDTO) {
            Users user = userRepository.findByUserId(updateSocialDTO.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            updateSocialDTO.updateEntity(user);

            userRepository.save(user);
        }

        //TODO : 첨부파일 기능 완성 이후에 프로필 사진 변경 기능 연결 할 것.
    //    public void updateByProfileImage(String userId, MultipartFile file) throws IOException {
    //        userRepository.findByUserId(socialUserDTO.getUserId())
    //                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    //
    //        Users updatedUser = Users.builder()
    //                .userId(socialUserDTO.getUserId())
    //                .profileUrl(socialUserDTO.getProfileUrl())
    //                .build();
    //
    //        userRepository.save(updatedUser);
    //    }

        @Transactional
        public void deleteUser(HttpServletResponse response, String userId) {
            Users user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            userRepository.delete(user);
            try {
                refreshRepository.deleteAllByUserId(userId);
            } catch (Exception e) {
                log.error("회원ID {} 에 대한 리프레시 토큰 삭제 중 오류가 발생했습니다.", userId, e);
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            CookieUtils.deleteCookie(response, "refresh");
        }
    }
