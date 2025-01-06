package com.elice.sdz.order.repository;

import com.elice.sdz.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    //특정 사용자의 모든 주문을 리스트로 반환
    Page<Order> findByUserEmail(String userEmail, Pageable pageable);

}
