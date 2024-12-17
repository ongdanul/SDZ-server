package com.elice.sdz.delivery.dto;

import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.user.entity.Users;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressDTO {

    private Long deliveryAddressId;

    @NotBlank
    private String userId;

    @NotBlank
    private String deliveryAddress1;

    @NotBlank
    private String deliveryAddress2;

    @NotBlank
    private String deliveryAddress3;

    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverContact;

    @NotBlank
    private String deliveryRequest;

    private boolean defaultCheck;

    public DeliveryAddress toEntity (Users user) {
        return DeliveryAddress.builder()
                .userId(user)
                .deliveryAddress1(deliveryAddress1)
                .deliveryAddress2(deliveryAddress2)
                .deliveryAddress3(deliveryAddress3)
                .receiverName(receiverName)
                .receiverContact(receiverContact)
                .deliveryRequest(deliveryRequest)
                .defaultCheck(defaultCheck)
                .build();
    }

    public void updateEntity(DeliveryAddress deliveryAddress, Users user) {
        deliveryAddress.setUserId(user);
        deliveryAddress.setDeliveryAddress1(deliveryAddress1);
        deliveryAddress.setDeliveryAddress2(deliveryAddress2);
        deliveryAddress.setDeliveryAddress3(deliveryAddress3);
        deliveryAddress.setReceiverName(receiverName);
        deliveryAddress.setReceiverContact(receiverContact);
        deliveryAddress.setDeliveryRequest(deliveryRequest);
        deliveryAddress.setDefaultCheck(defaultCheck);
    }
}
