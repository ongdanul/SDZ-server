package com.elice.sdz.delivery.controller.apiDocs;

import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
import com.elice.sdz.delivery.dto.DeliveryAddressListDTO;
import com.elice.sdz.user.dto.PageRequestDTO;
import com.elice.sdz.user.dto.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface DeliveryAddressApiDocs {

    @Operation(summary = "배송지 주소 목록 조회", description = "배송지 주소 목록을 조회하는 API입니다.")
    ResponseEntity<PageResponseDTO<DeliveryAddressListDTO>> deliveryAddressList(@ParameterObject PageRequestDTO pageRequestDTO);

    @Operation(summary = "배송지 주소 조회", description = "배송지 주소를 조회하는 API입니다.")
    ResponseEntity<DeliveryAddressDTO> deliveryAddressDetail(@PathVariable("deliveryAddressId") Long deliveryAddressId);

    @Operation(summary = "배송지 주소 생성", description = "배송지 주소를 생성하는 API입니다.")
    ResponseEntity<Void> createNewAddress(@RequestBody DeliveryAddressDTO deliveryAddressDTO);

    @Operation(summary = "배송지 주소 수정", description = "배송지 주소를 수정하는 API입니다.")
    ResponseEntity<String> updateAddress(@PathVariable("deliveryAddressId") Long deliveryAddressId, @RequestBody DeliveryAddressDTO deliveryAddressDTO);

    @Operation(summary = "기본 배송지 주소 설정", description = "기본 배송지로 설정하는 API입니다.")
    ResponseEntity<String> updateDefaultAddress(@PathVariable("deliveryAddressId") Long deliveryAddressId);

    @Operation(summary = "배송지 주소 삭제", description = "배송지 주소를 삭제하는 API입니다.")
    ResponseEntity<Void> deleteAddress(@PathVariable("deliveryAddressId") Long deliveryAddressId);
}
