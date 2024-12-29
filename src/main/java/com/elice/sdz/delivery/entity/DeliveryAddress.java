package com.elice.sdz.delivery.entity;

import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
//@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_address")
public class DeliveryAddress extends BaseEntity {

    @Id
    @Column(name = "delivery_address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryAddressId;

    @ManyToOne
    @JoinColumn(name = "email", nullable = false)
    private Users user;

    @Column(name = "delivery_address1", length = 20, nullable = false)
    private String deliveryAddress1;

    @Column(name = "delivery_address2", length = 50, nullable = false)
    private String deliveryAddress2;

    @Column(name = "delivery_address3", length = 50, nullable = false)
    private String deliveryAddress3;

    @Column(name = "receiver_name", length = 20, nullable = false)
    private String receiverName;

    @Column(name = "receiver_contact", length = 20, nullable = false)
    private String receiverContact;

    @Column(name = "delivery_request", length = 100, nullable = false)
    private String deliveryRequest;

    @Column(name = "default_check", nullable = false,
            columnDefinition = "BIT(1) DEFAULT 0")
    private boolean defaultCheck;
}
