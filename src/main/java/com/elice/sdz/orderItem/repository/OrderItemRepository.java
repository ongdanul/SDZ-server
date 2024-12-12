package com.elice.sdz.orderItem.repository;

import com.elice.sdz.orderItem.entity.OrderItem;
import com.elice.sdz.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByUserId(Users user);
}
