package com.elice.sdz.order.entity;

import ch.qos.logback.core.status.Status;
import com.elice.sdz.delivery.entity.Delivery;
import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
//@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_order")
public class Order extends BaseEntity {

    @Id //주문ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne //다대일
    @JoinColumn(name = "email", referencedColumnName = "email", nullable = false) // 외래 키 컬럼을 지정
    private Users user; // Users 엔티티와의 관계

    @ManyToOne //다대일
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;

    //일대다
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    //주문 총 가격
    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    //환불 가능여부
    @Column(name = "refund_status", nullable = false,
            columnDefinition = "BIT(1) DEFAULT 0")
    private boolean refundStatus; //환불 가능여부

    //주문 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private Status orderStatus; //주문 상태


    public enum Status { //주문 상태 나타내는 enum
        PENDING,
        PAYMENTPROCESSED,
        DELIVERYPROCESSED,
        REFUNDPROCESSED;
    }
}
