package com.elice.sdz.orderItem.service;

import com.elice.sdz.orderItem.entity.OrderItem;
import com.elice.sdz.orderItem.entity.OrderItemDetail;
import com.elice.sdz.orderItem.repository.OrderItemDetailRepository;
import com.elice.sdz.orderItem.repository.OrderItemRepository;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.user.entity.Users;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemDetailRepository orderItemDetailRepository;
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository; => db 재고 조회 후 넘어가지 않도록 하는 로직 추가

    // 유저 조회 메서드
    private Users findUserById(String userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return new Users();
    }

    // 장바구니 생성 및 찾기
    @Transactional
    public OrderItem findOrCreateOrderItem(String userId) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findByUserId(findUserById(userId));
        if (optionalOrderItem.isEmpty()) {
            OrderItem orderItem = new OrderItem();
//            orderItem.setUserId(userRepository.findById(userId)); // Users 객체 설정
            return orderItemRepository.save(orderItem);
        } else {
            return optionalOrderItem.get();
        }
    }

    // 장바구니 조회
    @Transactional
    public List<OrderItemDetail> getOrderItems(String userId) {
        OrderItem orderItem = orderItemRepository.findByUserId(findUserById(userId))
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));
        return orderItem.getOrderItemDetails();
    }

    // 장바구니 상품 추가
    @Transactional
    public void addOrderItem(String userId, Product product, int quantity) {
        OrderItem orderItem = findOrCreateOrderItem(userId);

        Optional<OrderItemDetail> optionalOrderItemDetail = orderItemDetailRepository
                .findByOrderItemIdAndProductId(orderItem.getId(), product.getProductId());

        if (optionalOrderItemDetail.isPresent()) {
            // 동일한 물건이 있을 경우 수량 수정
            OrderItemDetail orderItemDetail = optionalOrderItemDetail.get();
            orderItemDetail.setQuantity(orderItemDetail.getQuantity() + quantity);
            orderItemDetailRepository.save(orderItemDetail);
        } else {
            // 동일한 물건이 없을 경우 새로 추가
            OrderItemDetail orderItemDetail = new OrderItemDetail();
            orderItemDetail.setOrderItem(orderItem);
            orderItemDetail.setProduct(product);
            orderItemDetail.setQuantity(quantity);
            orderItemDetailRepository.save(orderItemDetail);
        }
    }

    // 장바구니 상품 삭제
    @Transactional
    public void deleteOrderItem(String userId, Product product, int quantity) {
        OrderItem orderItem = orderItemRepository.findByUserId(findUserById(userId))
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));

        Optional<OrderItemDetail> optionalOrderItemDetail = orderItemDetailRepository
                .findByOrderItemIdAndProductId(orderItem.getId(), product.getProductId());

        if (optionalOrderItemDetail.isPresent()) {
            OrderItemDetail orderItemDetail = optionalOrderItemDetail.get();
            if (orderItemDetail.getQuantity() > quantity) {
                // 수량만 감소
                orderItemDetail.setQuantity(orderItemDetail.getQuantity() - quantity);
                orderItemDetailRepository.save(orderItemDetail);
            } else {
                // 수량이 일치하거나 적을 경우 완전히 삭제
                orderItem.getOrderItemDetails().remove(orderItemDetail); // 리스트에서 제거
                orderItemDetailRepository.delete(orderItemDetail); // DB에서 삭제
            }
        }
    }

    // 장바구니 전체 삭제
    @Transactional
    public void clearOrderItems(String userId) {
        OrderItem orderItem = orderItemRepository.findByUserId(findUserById(userId))
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));
        orderItemDetailRepository.deleteAll(orderItem.getOrderItemDetails());
    }
}
