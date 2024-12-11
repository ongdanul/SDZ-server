package com.elice.sdz.refund.entity;

import com.elice.sdz.order.entity.Order;
import com.elice.sdz.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;

@Data
@Entity
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refund")
public class Refund {

    @Id
    @Column(name = "refund_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order orderId;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment paymentId;

    @Column(name = "refund_amount", nullable = false)
    private Double refundAmount;

    @Column(name = "refund_reason", length = 500, nullable = false)
    private String refundReason;

    @Column(name = "reg_date", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant regDate;

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
