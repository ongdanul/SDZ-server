package com.elice.sdz.delivery.repository;

import com.elice.sdz.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

}
