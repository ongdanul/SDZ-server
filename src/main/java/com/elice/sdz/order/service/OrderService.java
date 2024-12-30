package com.elice.sdz.order.service;

import com.elice.sdz.delivery.entity.Delivery;
import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.delivery.repository.DeliveryAddressRepository;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.order.dto.OrderDto;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.order.entity.OrderDetail;
import com.elice.sdz.order.repository.OrderDetailRepository;
import com.elice.sdz.order.repository.OrderRepository;
import com.elice.sdz.orderItem.dto.OrderItemDTO;
import com.elice.sdz.orderItem.entity.OrderItem;
import com.elice.sdz.orderItem.repository.OrderItemDetailRepository;
import com.elice.sdz.orderItem.repository.OrderItemRepository;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;


    //사용자 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(String userId) {
        //Users user = userRepository.findById(String.valueOf(userId))
                //.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return orderRepository.findByUserEmail(userId).stream().map(this::toDto).collect(Collectors.toList());
    }
    //특정(주문Id) 주문 상세 조회
    @Transactional(readOnly = true)
    public OrderDto findOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        return toDto(order);
    }

    //사용자 주문 추가
    @Transactional
    public OrderDto createOrder(OrderDto orderDto, String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (orderDto.getOrderItems() == null || orderDto.getOrderItems().isEmpty()) {
            throw new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }
        // 주문 세부 정보 리스트 생성
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderItemDTO orderItem : orderDto.getOrderItems()) {
            for (OrderItemDTO.OrderItemDetailDTO orderItemDetail : orderItem.getOrderItemDetails()) {
                int quantity = orderItemDetail.getQuantity();
                Product product = productRepository.findById(orderItemDetail.getProductId())
                        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
                OrderDetail orderDetail = OrderDetail.builder()
                        .orderCount(quantity)
                        .orderAmount(orderItemDetail.getProductAmount()) // 상품 가격
                        .product(product)
                        .build();
                orderDetails.add(orderDetail);
            }
        }
        // 배송 주소 생성 및 저장
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setUser(user);
        deliveryAddress.setDeliveryAddressId(orderDto.getDeliveryAddress().getDeliveryAddressId());
        deliveryAddress.setDeliveryAddress1(orderDto.getDeliveryAddress().getDeliveryAddress1());
        deliveryAddress.setDeliveryAddress2(orderDto.getDeliveryAddress().getDeliveryAddress2());
        deliveryAddress.setDeliveryAddress3(orderDto.getDeliveryAddress().getDeliveryAddress3());
        deliveryAddress.setReceiverName(orderDto.getDeliveryAddress().getReceiverName());
        deliveryAddress.setReceiverContact(orderDto.getDeliveryAddress().getReceiverContact());
        deliveryAddress.setDeliveryRequest(orderDto.getDeliveryAddress().getDeliveryRequest());
        deliveryAddress.setDefaultCheck(false);


        DeliveryAddress savedDeliveryAddress = deliveryAddressRepository.save(deliveryAddress);


        // 주문 객체 생성
        Order order = Order.builder()
                .user(user)
                .totalPrice(orderDto.getTotalPrice())
                .refundStatus(orderDto.isRefundStatus())
                .orderStatus(Order.Status.PENDING)
                .orderDetails(orderDetails)
                .regDate(Instant.now())
                .build();

        // 배송 정보 설정
        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryAddress(savedDeliveryAddress);
        delivery.setDeliveryStatus(Delivery.Status.PENDING);
        order.setDelivery(delivery);

        // 주문 세부 정보와 주문 연결
        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order);
        }
        // 주문 저장
        Order savedOrder = orderRepository.save(order);
        return toDto(savedOrder);
    }

    //사용자 주문 수정
    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getOrderStatus() != Order.Status.PENDING) {
            throw new CustomException(ErrorCode.ORDER_CANNOT_BE_MODIFIED);
        }

        OrderDetail orderDetail = orderDetailRepository.findByOrder(order)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_DETAIL_NOT_FOUND));


        order.setRefundStatus(orderDto.isRefundStatus());

        orderDetailRepository.save(orderDetail);
        Order updatedOrder = orderRepository.save(order);
        return toDto(updatedOrder);
    }

    //사용자 주문취소
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        orderRepository.delete(order);
    }

    //관리자 모든 주문 조회
    @Transactional(readOnly = true)
    public List<OrderDto> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    //관리자 주문 상태수정
    @Transactional
    public OrderDto updateOrderStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        order.setOrderStatus(status);
        return toDto(orderRepository.save(order));
    }
    //Entity->toDto 변환
    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();

        // 기본 필드 매핑
        dto.setOrderId(order.getOrderId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setRefundStatus(order.isRefundStatus());
        dto.setRegDate(order.getRegDate());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setEmail(order.getUser().getEmail());

        // OrderItemDTO 리스트 생성
        List<OrderItemDTO> orderItems = new ArrayList<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            OrderItemDTO orderItemDTO = createOrderItemDTO(orderDetail);
            orderItems.add(orderItemDTO);
        }

        dto.setOrderItems(orderItems);

        return dto;
    }

    private OrderItemDTO createOrderItemDTO(OrderDetail orderDetail) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();

        // OrderItemDTO의 orderItemDetails 리스트 생성
        List<OrderItemDTO.OrderItemDetailDTO> orderItemDetails = new ArrayList<>();

        // OrderDetail에서 OrderItemDetailDTO로 변환
        OrderItemDTO.OrderItemDetailDTO orderItemDetailDTO = new OrderItemDTO.OrderItemDetailDTO();
        orderItemDetailDTO.setProductId(orderDetail.getProduct().getProductId());  // 제품 ID
        orderItemDetailDTO.setQuantity(orderDetail.getOrderCount());  // 주문 수량
        orderItemDetailDTO.setProductName(orderDetail.getProduct().getProductName());  // 제품 이름
        orderItemDetailDTO.setProductAmount(orderDetail.getOrderAmount());  // 주문 금액
        orderItemDetailDTO.setThumbnailPath(orderDetail.getProduct().getThumbnailPath());  // 썸네일 경로

        // 변환된 OrderItemDetailDTO를 orderItemDetails 리스트에 추가
        orderItemDetails.add(orderItemDetailDTO);

        // 변환된 orderItemDetails를 OrderItemDTO에 설정
        orderItemDTO.setOrderItemDetails(orderItemDetails);

        return orderItemDTO;
    }

}
