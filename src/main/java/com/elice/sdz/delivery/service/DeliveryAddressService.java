package com.elice.sdz.delivery.service;

import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
import com.elice.sdz.delivery.dto.DeliveryAddressListDTO;
import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.delivery.repository.DeliveryAddressRepository;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.PageRequestDTO;
import com.elice.sdz.user.dto.PageResponseDTO;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

    public PageResponseDTO<DeliveryAddressListDTO> deliveryAddressList(PageRequestDTO pageRequestDTO, String email) {
        Pageable pageable = pageRequestDTO.getPageable("defaultCheck", "createdAt");

        Page<DeliveryAddress> result = findAddressesByEmail(email, pageable);

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

    private Page<DeliveryAddress> findAddressesByEmail(String email, Pageable pageable) {
        Users user = userRepository.findById(email)
                .orElseThrow(() ->  new CustomException(ErrorCode.USER_NOT_FOUND));
        return deliveryAddressRepository.findAllByUser(user, pageable);
    }

    public DeliveryAddressDTO findDeliveryAddressInfo(Long deliveryAddressId, String email) {
        Users user = userRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId)
                .orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        return DeliveryAddressDTO.toDTO(deliveryAddress, user);
    }

    public void createNewAddress (DeliveryAddressDTO deliveryAddressDTO) {
        Users user = userRepository.findById(deliveryAddressDTO.getEmail())
                .orElseThrow(() ->  new CustomException(ErrorCode.USER_NOT_FOUND));

        long addressCount = deliveryAddressRepository.countByUser(user);
        if (addressCount >= 10) {
            throw new CustomException(ErrorCode.MAX_DELIVERY_ADDRESSES);
        }

        if (deliveryAddressDTO.isDefaultCheck()) {
            updateAllDefaultAddresses(user);
        }

        DeliveryAddress deliveryAddress = deliveryAddressDTO.toEntity(user);
        try {
            log.info("새 배송 주소가 성공적으로 생성되었습니다.");
            deliveryAddressRepository.save(deliveryAddress);
        } catch (Exception e) {
            log.error("배송 주소 생성 중 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateAddress(Long deliveryAddressId, DeliveryAddressDTO deliveryAddressDTO) {
        Users user = userRepository.findById(deliveryAddressDTO.getEmail())
                .orElseThrow(() ->  new CustomException(ErrorCode.USER_NOT_FOUND));

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId)
                .orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        if (!deliveryAddress.getUser().getEmail().equals(user.getEmail())) {
            throw new AccessDeniedException("이 주소를 수정할 권한이 없습니다.");
        }

        if (deliveryAddressDTO.isDefaultCheck()) {
            updateAllDefaultAddresses(user);
        }

        deliveryAddressDTO.updateEntity(deliveryAddress, user);
        try {
            log.info("배송 주소가 성공적으로 수정되었습니다.");
            deliveryAddressRepository.save(deliveryAddress);
        } catch (Exception e) {
            log.error("배송 주소 수정 중 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateDefaultCheck(Long deliveryAddressId, String email){
        Users user = userRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId)
                .orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        if (!deliveryAddress.getUser().getEmail().equals(user.getEmail())) {
            throw new AccessDeniedException("이 주소를 수정할 권한이 없습니다.");
        }

        updateAllDefaultAddresses(user);

        deliveryAddress.setDefaultCheck(true);

        try {
            log.info("배송 주소가 성공적으로 수정되었습니다.");
            deliveryAddressRepository.save(deliveryAddress);
        } catch (Exception e) {
            log.error("배송 주소 수정 중 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void updateAllDefaultAddresses(Users user) {
        List<DeliveryAddress> deliveryAddressList = deliveryAddressRepository.findAllByUser(user);
        deliveryAddressList.forEach(addr -> {
            if (addr.isDefaultCheck()) {
                addr.setDefaultCheck(false);
            }
        });
        deliveryAddressRepository.saveAll(deliveryAddressList);
    }

    @Transactional
    public void deleteAddress(Long deliveryAddressId, String email) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId)
                .orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        if(!deliveryAddress.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("삭제할 권한이 없습니다.");
        }
        log.info("배송 주소가 성공적으로 삭제되었습니다.");
        deliveryAddressRepository.delete(deliveryAddress);
    }
}
