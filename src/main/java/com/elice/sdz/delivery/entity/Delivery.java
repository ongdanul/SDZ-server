package com.elice.sdz.delivery.entity;

import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery")
    public class Delivery extends BaseEntity {

    @Id
    @Column(name = "delivery_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    @ManyToOne
    @JoinColumn(name = "delivery_address_id", nullable = false)
    private DeliveryAddress deliveryAddress;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private Status deliveryStatus;

    public enum Status {
        PENDING,
        PROCESSING,
        PROCESSED;
    }

}
