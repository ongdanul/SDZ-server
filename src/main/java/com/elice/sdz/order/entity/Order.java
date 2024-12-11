package com.elice.sdz.order.entity;

import com.elice.sdz.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_order")
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

    @Column(name = "order_count", nullable = false)
    private int orderCount;

    @Column(name = "order_amount", nullable = false)
    private Double orderAmount;

    @Column(name = "reg_date", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant regDate;

    @Column(name = "refund_status", nullable = false,
            columnDefinition = "BIT(1) DEFAULT 0")
    private boolean refundStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private Status orderStatus;

    public enum Status {
        PENDING,
        PAYMENTPROCESSED,
        DELIVERYPROCESSED,
        REFUNDPROCESSED;
    }
}
