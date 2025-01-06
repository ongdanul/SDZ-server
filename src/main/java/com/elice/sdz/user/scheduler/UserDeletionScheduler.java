package com.elice.sdz.user.scheduler;

import com.elice.sdz.delivery.repository.DeliveryRepository;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.SocialRepository;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletionScheduler {
    private final UserRepository userRepository;
    private final SocialRepository socialRepository;
    private final DeliveryRepository deliveryRepository;

    @Scheduled(cron = "0 0 4 L * ?")
    @Transactional
    public void deleteInactiveUsers() {
        log.debug("회원 탈퇴 스케줄러 시작");

        ZonedDateTime oneYearAgoZoned = ZonedDateTime.now().minusYears(1);
        Instant oneYearAgo = oneYearAgoZoned.toInstant();
        List<Users> inactiveUsers = userRepository.findByDeactivatedTrueAndDeactivationTimeBefore(oneYearAgo);

        inactiveUsers.forEach(user -> {
            // 탈퇴 조건: 탈퇴 후 1년 이상된 회원
            try {
                if(user.isSocial()){
                    socialRepository.deleteByUser(user);
                }
                for (Order order : user.getOrders()) {
                    if (order.getDelivery() != null) {
                        deliveryRepository.delete(order.getDelivery());
                    }
                }
                userRepository.delete(user);
                log.info("탈퇴 후 1년이 지난 회원 {}의 정보가 삭제되었습니다.", user.getEmail());
            } catch (Exception e) {
                log.error("회원 {} 삭제 중 오류가 발생했습니다.", user.getEmail(), e);
            }
        });
        log.debug("회원 탈퇴 스케줄러 실행 완료");
    }
}


