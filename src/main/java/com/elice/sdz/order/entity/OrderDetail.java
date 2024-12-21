package com.elice.sdz.order.entity;

import com.elice.sdz.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_order_detail")
public class OrderDetail extends BaseEntity {

    @Id //기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @ManyToOne //order 다대일
    @JoinColumn(name = "order_id")
    private Order order;

    //주문 수량
    @Column(name = "order_count", nullable = false)
    private int orderCount;

    //주문 금액
    @Column(name = "order_amount", nullable = false)
    private Double orderAmount;

}
