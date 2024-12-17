package com.elice.sdz.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressDTO {

    private Long deliveryAddressId;

    private String userId;

    private String deliveryAddress1;

    private String deliveryAddress2;

    private String deliveryAddress3;

    private String receiverName;

    private String receiverContact;

    private String deliveryRequest;

    private boolean defaultCheck;
}
