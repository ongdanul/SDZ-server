package com.elice.sdz.refund.entity;

import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;

@Getter
@Setter
//@ToString
@Entity
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refund")
public class Refund extends BaseEntity {

    @Id
    @Column(name = "refund_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "refund_amount", nullable = false)
    private Double refundAmount;

    @Column(name = "refund_reason", length = 500, nullable = false)
    private String refundReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    private Status refundStatus;

    public enum Status {
        PENDING,
        PROCESSING,
        PROCESSED,
        CANCELED;
    }
}
