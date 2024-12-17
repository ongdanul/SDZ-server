package com.elice.sdz.delivery.controller;

import com.elice.sdz.delivery.controller.apiDocs.DeliveryAddressApiDocs;
import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
import com.elice.sdz.delivery.dto.DeliveryAddressListDTO;
import com.elice.sdz.delivery.service.DeliveryAddressService;
import com.elice.sdz.user.dto.PageRequestDTO;
import com.elice.sdz.user.dto.PageResponseDTO;
import com.elice.sdz.user.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveryAddress")
public class DeliveryAddressController implements DeliveryAddressApiDocs {

    private final DeliveryAddressService deliveryAddressService;
    private final AuthenticationService authenticationService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<DeliveryAddressListDTO>> deliveryAddressList(@ParameterObject PageRequestDTO pageRequestDTO) {
        String userId = authenticationService.getCurrentUserId();
        PageResponseDTO<DeliveryAddressListDTO> response = deliveryAddressService.deliveryAddressList(pageRequestDTO, userId);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    @PreAuthorize("#deliveryAddressDTO.userId == authentication.name")
    public ResponseEntity<Void> createNewAddress(@RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        deliveryAddressDTO.setUserId(authenticationService.getCurrentUserId());
        deliveryAddressService.createNewAddress(deliveryAddressDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{deliveryAddressId}")
    @PreAuthorize("#deliveryAddressDTO.userId == authentication.name")
    public ResponseEntity<String> updateAddress(@PathVariable("deliveryAddressId") Long deliveryAddressId, @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        deliveryAddressDTO.setUserId(authenticationService.getCurrentUserId());
        deliveryAddressService.updateAddress(deliveryAddressId, deliveryAddressDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{deliveryAddressId}")
    @PreAuthorize("#userId == authentication.name")
    public ResponseEntity<Void> deleteAddress(@PathVariable("deliveryAddressId") Long deliveryAddressId) {
        String userId = authenticationService.getCurrentUserId();
        deliveryAddressService.deleteAddress(deliveryAddressId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
