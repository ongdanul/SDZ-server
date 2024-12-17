package com.elice.sdz.global.jwt;

import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.elice.sdz.global.config.SecurityConstants.RESET_PERIOD;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureResetScheduler {
    private final UserRepository userRepository;
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetLoginAttempts() {
        log.debug("로그인 실패 횟수 초기화 스케줄러 시작");

        List<Users> users = userRepository.findByLastFailedLoginIsNotNull();
        long now = System.currentTimeMillis();

        users.forEach(user -> {
            // 로그인 실패 횟수 초기화 조건:
            // - 로그인 잠금 상태가 아닌 경우
            // - 마지막 실패 시간에서 3일 이상 지난 경우
            // - LoginAttempts가 5 미만일 경우에만 초기화
            if (!user.isLoginLock() && user.getLastFailedLogin() != null &&
                    now - user.getLastFailedLogin().toEpochMilli() > RESET_PERIOD) {
                if (user.getLoginAttempts() < 5) {
                    user.setLoginAttempts(0);
                    try {
                        userRepository.save(user);
                        //TODO 로그인 기능 완성후 삭제하기
                        log.info("Test - 회원 {} 실패 횟수 초기화 완료", user.getUserId());
                    } catch (Exception e) {
                        log.error("회원의 로그인 실패 횟수 초기화 중 오류 발생", e);
                    }
                }
            }
        });
        log.debug("로그인 실패 횟수 초기화 스케줄러 실행 완료");
    }
}
