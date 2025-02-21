package com.elice.sdz.orderItem.service;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.orderItem.dto.OrderItemDTO;
import com.elice.sdz.orderItem.dto.OrderItemModifyDTO;
import com.elice.sdz.orderItem.entity.OrderItem;
import com.elice.sdz.orderItem.entity.OrderItemDetail;
import com.elice.sdz.orderItem.repository.OrderItemDetailRepository;
import com.elice.sdz.orderItem.repository.OrderItemRepository;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
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

        // 삭제할 orderItemDetail
        List<OrderItemDetail> detailsToRemove = new ArrayList<>();

        for (OrderItemDetail detail : new ArrayList<>(orderItem.getOrderItemDetails())) {
            Product product = detail.getProduct();
            if (product.getProductCount() <= 0) {
                // 재고가 없는 상품 삭제 목록에 추가
                detailsToRemove.add(detail);
            } else if (product.getProductCount() < detail.getQuantity()) {
                // 재고보다 수량이 많으면 조정
                detail.setQuantity(product.getProductCount());
                orderItemDetailRepository.save(detail);
            }
        }

        // 삭제 작업 처리
        for (OrderItemDetail detail : detailsToRemove) {
            orderItemDetailRepository.delete(detail);
            orderItem.getOrderItemDetails().remove(detail);
        }

        orderItem.updateTimestamp();
        orderItemRepository.save(orderItem);

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

        int currentQuantity = optionalOrderItemDetail.map(OrderItemDetail::getQuantity).orElse(0); // 기존 수량

        if (addProduct.getProductCount() < currentQuantity + quantity) {
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

    // 게스트 장바구니 병합
    @Transactional
    public void mergeOrderItems(String userId, OrderItemDTO guestOrderItems) {
        OrderItem userOrderItem = findOrCreateOrderItem(userId);

        for (OrderItemDTO.OrderItemDetailDTO guestDetail : guestOrderItems.getOrderItemDetails()) {
            Product product = findByProductId(guestDetail.getProductId());

            Optional<OrderItemDetail> existingDetail = orderItemDetailRepository
                    .findByOrderItemIdAndProduct(userOrderItem.getId(), product);

            int finalQuantity;
            if (existingDetail.isPresent()) {
                // 이미 존재하는 상품이 있을 경우 수량 증가
                OrderItemDetail detail = existingDetail.get();
                finalQuantity = detail.getQuantity() + guestDetail.getQuantity();

                // 서버 재고보다 많아지면 재고 수량으로 조정
                if (finalQuantity > product.getProductCount()) {
                    finalQuantity = product.getProductCount();
                }

                detail.setQuantity(finalQuantity);
                orderItemDetailRepository.save(detail);
            } else {
                // 새 상품 추가
                finalQuantity = guestDetail.getQuantity();

                // 서버 재고보다 많아지면 재고 수량으로 조정
                if (finalQuantity > product.getProductCount()) {
                    finalQuantity = product.getProductCount();
                }

                OrderItemDetail newDetail = new OrderItemDetail();
                newDetail.setOrderItem(userOrderItem);
                newDetail.setProduct(product);
                newDetail.setQuantity(finalQuantity);
                newDetail.setProductAmount(product.getProductAmount());
                orderItemDetailRepository.save(newDetail);
            }
        }

        userOrderItem.updateTimestamp();
        orderItemRepository.save(userOrderItem);
    }
}
