package com.elice.sdz.delivery.entity;

import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
import com.elice.sdz.delivery.dto.DeliveryAddressListDTO;
import com.elice.sdz.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_address")
public class DeliveryAddress {

    @Id
    @Column(name = "delivery_address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryAddressId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

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

    public static DeliveryAddress deliveryAddressToEntity (DeliveryAddressDTO dto, Users user) {
        return DeliveryAddress.builder()
                .userId(user)
                .deliveryAddress1(dto.getDeliveryAddress1())
                .deliveryAddress2(dto.getDeliveryAddress2())
                .deliveryAddress3(dto.getDeliveryAddress3())
                .receiverName(dto.getReceiverName())
                .receiverContact(dto.getReceiverContact())
                .deliveryRequest(dto.getDeliveryRequest())
                .defaultCheck(dto.isDefaultCheck())
                .build();
    }
}
