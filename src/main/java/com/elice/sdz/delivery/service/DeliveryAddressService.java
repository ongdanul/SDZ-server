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
                .orElseThrow(() ->  new CustomException(ErrorCode.USER_NOT_FOUND));
        return deliveryAddressRepository.findAllByUserId(user, pageable);
    }

    public void createNewAddress (DeliveryAddressDTO deliveryAddressDTO) {
        Users user = userRepository.findByUserId(deliveryAddressDTO.getUserId())
                .orElseThrow(() ->  new CustomException(ErrorCode.USER_NOT_FOUND));

        DeliveryAddress deliveryAddress = deliveryAddressDTO.toEntity(user);
        try {
            deliveryAddressRepository.save(deliveryAddress);
            log.info("새 배송 주소가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            log.error("배송 주소 생성 중 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateAddress(Long deliveryAddressId, DeliveryAddressDTO deliveryAddressDTO) {
        Users user = userRepository.findByUserId(deliveryAddressDTO.getUserId())
                .orElseThrow(() ->  new CustomException(ErrorCode.USER_NOT_FOUND));

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId)
                .orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        if (!deliveryAddress.getUserId().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("이 주소를 수정할 권한이 없습니다.");
        }

        deliveryAddressDTO.updateEntity(deliveryAddress, user);
        try {
            deliveryAddressRepository.save(deliveryAddress);
            log.info("배송 주소가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("배송 주소 수정 중 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void deleteAddress(Long deliveryAddressId, String userId) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByDeliveryAddressId(deliveryAddressId)
                .orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        if(!deliveryAddress.getUserId().getUserId().equals(userId)) {
            throw new AccessDeniedException("삭제할 권한이 없습니다.");
        }
        deliveryAddressRepository.delete(deliveryAddress);
        log.info("배송 주소가 성공적으로 삭제되었습니다.");
    }
}
