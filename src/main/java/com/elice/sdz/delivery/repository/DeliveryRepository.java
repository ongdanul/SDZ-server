package com.elice.sdz.delivery.repository;

import com.elice.sdz.delivery.entity.Delivery;
import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByDeliveryAddress(DeliveryAddress deliveryAddress);
}
