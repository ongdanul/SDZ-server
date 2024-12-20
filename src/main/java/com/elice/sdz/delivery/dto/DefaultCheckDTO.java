package com.elice.sdz.delivery.dto;

import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultCheckDTO {

    private Long deliveryAddressId;

    private String email;

    private boolean defaultCheck;

    public void updateEntity(DeliveryAddress deliveryAddress, Users user) {
        deliveryAddress.setUser(user);
        deliveryAddress.setDefaultCheck(defaultCheck);
    }
}
