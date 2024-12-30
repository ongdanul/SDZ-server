package com.elice.sdz.order.repository;

import com.elice.sdz.order.entity.Order;
import com.elice.sdz.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    Optional<OrderDetail> findByOrder(Order order);

    // 주문 상세 정보 삭제
    //void deleteByOrder(Order order);

    // 주문 상세 정보 존재 여부 확인
    //boolean existsByOrder(Order order);
}

