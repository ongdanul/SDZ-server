package com.elice.sdz.delivery.dto;

import com.elice.sdz.delivery.entity.DeliveryAddress;
import lombok.Data;

@Data
public class DeliveryAddressListDTO {

    private Long deliveryAddressId;

    private String userId;

    private String deliveryAddress1;

    private String deliveryAddress2;

    private String deliveryAddress3;

    private String receiverName;

    private String receiverContact;

    private String deliveryRequest;

    private boolean defaultCheck;

    public DeliveryAddressListDTO(DeliveryAddress deliveryAddress) {
        this.deliveryAddressId = deliveryAddress.getDeliveryAddressId();
        this.userId = deliveryAddress.getUser().getEmail();
        this.deliveryAddress1 = deliveryAddress.getDeliveryAddress1();
        this.deliveryAddress2 = deliveryAddress.getDeliveryAddress2();
        this.deliveryAddress3 = deliveryAddress.getDeliveryAddress3();
        this.receiverName = deliveryAddress.getReceiverName();
        this.receiverContact = deliveryAddress.getReceiverContact();
        this.deliveryRequest = deliveryAddress.getDeliveryRequest();
        this.defaultCheck = deliveryAddress.isDefaultCheck();
    }
}
