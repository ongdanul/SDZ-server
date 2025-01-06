package com.elice.sdz.delivery.controller;

import com.elice.sdz.delivery.controller.apiDocs.DeliveryAddressApiDocs;
import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
import com.elice.sdz.delivery.dto.DeliveryAddressListDTO;
import com.elice.sdz.delivery.service.DeliveryAddressService;
import com.elice.sdz.user.dto.request.PageRequestDTO;
import com.elice.sdz.user.dto.response.PageResponseDTO;
import com.elice.sdz.user.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        String email = authenticationService.getCurrentUser();
        PageResponseDTO<DeliveryAddressListDTO> response = deliveryAddressService.deliveryAddressList(pageRequestDTO, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deliveryAddressId}")
    public ResponseEntity<DeliveryAddressDTO> deliveryAddressDetail(@PathVariable("deliveryAddressId") Long deliveryAddressId) {
        String email = authenticationService.getCurrentUser();
        DeliveryAddressDTO deliveryAddressDTO = deliveryAddressService.findDeliveryAddressInfo(deliveryAddressId, email);
        return ResponseEntity.ok(deliveryAddressDTO);
    }

    @GetMapping("/default")
    public ResponseEntity<DeliveryAddressDTO> deliveryAddressDetail() {
        String email = authenticationService.getCurrentUser();
        DeliveryAddressDTO deliveryAddressDTO = deliveryAddressService.findDeliveryAddressDefaultInfo(email);
        return ResponseEntity.ok(deliveryAddressDTO);
    }

    @PostMapping
    public ResponseEntity<Void> createNewAddress(@RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        deliveryAddressDTO.setEmail(authenticationService.getCurrentUser());
        deliveryAddressService.createNewAddress(deliveryAddressDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{deliveryAddressId}")
    public ResponseEntity<String> updateAddress(@PathVariable("deliveryAddressId") Long deliveryAddressId, @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        deliveryAddressDTO.setEmail(authenticationService.getCurrentUser());
        deliveryAddressService.updateAddress(deliveryAddressId, deliveryAddressDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{deliveryAddressId}/default")
    public ResponseEntity<String> updateDefaultAddress(@PathVariable("deliveryAddressId") Long deliveryAddressId) {
        String email = authenticationService.getCurrentUser();
        deliveryAddressService.updateDefaultCheck(deliveryAddressId, email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{deliveryAddressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("deliveryAddressId") Long deliveryAddressId) {
        String email = authenticationService.getCurrentUser();
        deliveryAddressService.deleteAddress(deliveryAddressId, email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
