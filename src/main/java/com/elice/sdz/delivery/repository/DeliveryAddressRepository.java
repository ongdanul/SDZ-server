package com.elice.sdz.delivery.repository;

import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    Page<DeliveryAddress> findAllByUserId(Users userId, Pageable pageable);

    Optional<DeliveryAddress> findByDeliveryAddressId(Long deliveryAddressId);
}
