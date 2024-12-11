package com.elice.sdz.payment.entity;

import com.elice.sdz.order.entity.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
public class Payment {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order orderId;

    @Column(name = "payment_amount", nullable = false)
    private Double paymentAmount;

    @Column(name = "payment_method", length = 50, nullable = false)
    private String paymentMethod;

    @Column(name = "reg_date", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant regDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private Status paymentStatus;

    public enum Status {
        PENDING,
        SUCCESS,
        FAILURE;
    }
}
