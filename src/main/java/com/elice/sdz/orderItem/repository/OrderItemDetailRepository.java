package com.elice.sdz.orderItem.repository;

import com.elice.sdz.orderItem.entity.OrderItemDetail;
import com.elice.sdz.product.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemDetailRepository extends JpaRepository<OrderItemDetail, Long> {
    Optional<OrderItemDetail> findByOrderItemIdAndProduct(Long orderItemId, Product product);

    List<OrderItemDetail> findByProduct(Product product);
}