package com.elice.sdz.orderItem.service;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.orderItem.dto.OrderItemDTO;
import com.elice.sdz.orderItem.entity.OrderItem;
import com.elice.sdz.orderItem.entity.OrderItemDetail;
import com.elice.sdz.orderItem.repository.OrderItemDetailRepository;
import com.elice.sdz.orderItem.repository.OrderItemRepository;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemDetailRepository orderItemDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 유저 조회 메서드
    private Users findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 상품 조회 메서드
    private Product findByProductId(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    // 장바구니 조회 (DTO 반환)
    @Transactional
    public OrderItemDTO getOrderItems(String userId) {
        OrderItem orderItem = orderItemRepository.findByUser(findUserById(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND));

        return convertToDTO(orderItem);
    }

    // DTO 변환 메서드
    private OrderItemDTO convertToDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderItemId(orderItem.getId());

        List<OrderItemDTO.OrderItemDetailDTO> details = orderItem.getOrderItemDetails().stream()
                .map(detail -> {
                    OrderItemDTO.OrderItemDetailDTO detailDTO = new OrderItemDTO.OrderItemDetailDTO();
                    detailDTO.setProductId(detail.getProduct().getProductId());
                    detailDTO.setProductName(detail.getProduct().getProductName());
                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setProductAmount(detail.getProductAmount());
                    detailDTO.setThumbnailPath(detail.getProduct().getThumbnailPath());
                    return detailDTO;
                }).collect(Collectors.toList());

        orderItemDTO.setOrderItemDetails(details);
        return orderItemDTO;
    }

    // 장바구니 상품 추가
    @Transactional
    public void addOrderItem(String userId, Long productId, int quantity) {
        OrderItem orderItem = findOrCreateOrderItem(userId);
        Product addProduct = findByProductId(productId);

        Optional<OrderItemDetail> optionalOrderItemDetail = orderItemDetailRepository
                .findByOrderItemIdAndProduct(orderItem.getId(), addProduct);

        if (addProduct.getProductCount() - quantity < 0) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK); // 재고 초과 시 예외 발생
        }

        if (optionalOrderItemDetail.isPresent()) {
            // 동일한 물건이 있을 경우 수량 수정
            OrderItemDetail orderItemDetail = optionalOrderItemDetail.get();
            orderItemDetail.setQuantity(orderItemDetail.getQuantity() + quantity);
            orderItemDetailRepository.save(orderItemDetail);
        } else {
            // 동일한 물건이 없을 경우 새로 추가
            OrderItemDetail orderItemDetail = new OrderItemDetail();
            orderItemDetail.setOrderItem(orderItem);
            orderItemDetail.setProduct(addProduct);
            orderItemDetail.setQuantity(quantity);
            orderItemDetail.setProductAmount(addProduct.getProductAmount());
            orderItemDetailRepository.save(orderItemDetail);
        }

        orderItem.updateTimestamp();
        orderItemRepository.save(orderItem);
    }

    // 장바구니 상품 삭제
    @Transactional
    public void deleteOrderItem(String userId, Long productId, int quantity) {
        OrderItem orderItem = orderItemRepository.findByUser(findUserById(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND));
        Product deleteProduct = findByProductId(productId);

        Optional<OrderItemDetail> optionalOrderItemDetail = orderItemDetailRepository
                .findByOrderItemIdAndProduct(orderItem.getId(), deleteProduct);

        if (optionalOrderItemDetail.isPresent()) {
            OrderItemDetail orderItemDetail = optionalOrderItemDetail.get();

            // 수량 감소 처리
            int updatedQuantity = orderItemDetail.getQuantity() - quantity;
            if (updatedQuantity <= 0) {
                // 수량이 0 이하일 경우 해당 항목 삭제
                orderItemDetailRepository.delete(orderItemDetail);
                orderItem.getOrderItemDetails().remove(orderItemDetail);
            } else {
                // 수량이 남아 있을 경우 업데이트
                orderItemDetail.setQuantity(updatedQuantity);
                orderItemDetailRepository.save(orderItemDetail);
            }

            orderItem.updateTimestamp();
            orderItemRepository.save(orderItem);
        }
    }


    // 장바구니 전체 삭제
    @Transactional
    public void clearOrderItems(String userId) {
        OrderItem orderItem = orderItemRepository.findByUser(findUserById(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND));

        orderItem.getOrderItemDetails().clear();
        orderItem.updateTimestamp();
        orderItemRepository.save(orderItem);
    }

    // 장바구니 생성 및 찾기
    private OrderItem findOrCreateOrderItem(String userId) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findByUser(findUserById(userId));
        if (optionalOrderItem.isEmpty()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setUser(findUserById(userId));
            return orderItemRepository.save(orderItem);
        } else {
            return optionalOrderItem.get();
        }
    }
}
