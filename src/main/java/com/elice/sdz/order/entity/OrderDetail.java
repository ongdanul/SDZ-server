package com.elice.sdz.order.entity;

import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product_order_detail")
public class OrderDetail extends BaseEntity {

    @Id //기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    //주문 수량
    @Column(name = "order_count", nullable = false)
    private int orderCount;

    //주문 금액
    @Column(name = "order_amount")
    private Double orderAmount;

    @ManyToOne //order 다대일
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

}
