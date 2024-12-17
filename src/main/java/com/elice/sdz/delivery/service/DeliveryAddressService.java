package com.elice.sdz.delivery.service;

import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
import com.elice.sdz.delivery.dto.DeliveryAddressListDTO;
import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.delivery.repository.DeliveryAddressRepository;
import com.elice.sdz.user.dto.PageRequestDTO;
import com.elice.sdz.user.dto.PageResponseDTO;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final UserRepository userRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;

    public PageResponseDTO<DeliveryAddressListDTO> deliveryAddressList(PageRequestDTO pageRequestDTO, String userId) {
        Pageable pageable = pageRequestDTO.getPageable();

        Page<DeliveryAddress> result = findAddressesByUserId(userId, pageable);

        List<DeliveryAddressListDTO> dtoList = result.getContent()
                .stream()
                .map(DeliveryAddressListDTO::new)
                .collect(Collectors.toList());

        return PageResponseDTO.<DeliveryAddressListDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .keyword(pageRequestDTO.getKeyword())
                .build();
    }

    private Page<DeliveryAddress> findAddressesByUserId(String userId, Pageable pageable) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return deliveryAddressRepository.findAllByUserId(user, pageable);
    }

    public void createNewAddress (DeliveryAddressDTO deliveryAddressDTO) {
        Users user = userRepository.findByUserId(deliveryAddressDTO.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressToEntity(deliveryAddressDTO, user);
        try {
            deliveryAddressRepository.save(deliveryAddress);
            log.info("Create New DeliveryAddress successfully");
        } catch (Exception e) {
            log.error("Error occurred during the create New deliveryAddress process: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred during the create New deliveryAddress process.");
        }
    }

    @Transactional
    public void updateAddress(Long deliveryAddressId, DeliveryAddressDTO deliveryAddressDTO) {
        Users user = userRepository.findByUserId(deliveryAddressDTO.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId)
                .orElseThrow(() -> new IllegalArgumentException("DeliveryAddress not found"));

        if (!deliveryAddress.getUserId().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You do not have permission to update this address");
        }

        deliveryAddress.setUserId(user);
        deliveryAddress.setDeliveryAddress1(deliveryAddressDTO.getDeliveryAddress1());
        deliveryAddress.setDeliveryAddress2(deliveryAddressDTO.getDeliveryAddress2());
        deliveryAddress.setDeliveryAddress3(deliveryAddressDTO.getDeliveryAddress3());
        deliveryAddress.setReceiverName(deliveryAddressDTO.getReceiverName());
        deliveryAddress.setReceiverContact(deliveryAddressDTO.getReceiverContact());
        deliveryAddress.setDeliveryRequest(deliveryAddressDTO.getDeliveryRequest());
        deliveryAddress.setDefaultCheck(deliveryAddressDTO.isDefaultCheck());
        try {
            deliveryAddressRepository.save(deliveryAddress);
            log.info("Update DeliveryAddress successfully");
        } catch (Exception e) {
            log.error("Error occurred during the update deliveryAddress process: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred during the update deliveryAddress process.");
        }
    }

    @Transactional
    public void deleteAddress(Long deliveryAddressId, String userId) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId)
                .orElseThrow(() -> new IllegalArgumentException("DeliveryAddress not found"));

        if(!deliveryAddress.getUserId().getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this address");
        }
        deliveryAddressRepository.delete(deliveryAddress);
    }
}
