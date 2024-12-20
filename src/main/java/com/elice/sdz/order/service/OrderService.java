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
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;



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
        Order order = new Order();
        order.setUser(user);
        order.setRefundStatus(orderDto.isRefundStatus());
        order.setOrderStatus(Order.Status.PENDING);

        DeliveryAddress deliveryAddress = orderDto.getDeliveryAddress().toEntity(user);
        DeliveryAddress savedDeliveryAddress = deliveryAddressRepository.save(deliveryAddress);

        // 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setDeliveryAddress(savedDeliveryAddress);
        delivery.setOrder(order);
        delivery.setDeliveryStatus(Delivery.Status.PENDING);

        Order savedOrder = orderRepository.save(order);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(savedOrder);
        orderDetail.setOrderCount(orderDto.getOrderCount());
        orderDetail.setOrderAmount(orderDto.getOrderAmount());
        orderDetailRepository.save(orderDetail);

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

        orderDetail.setOrderCount(orderDto.getOrderCount());
        orderDetail.setOrderAmount(orderDto.getOrderAmount());
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
        dto.setOrderId(order.getOrderId());
        //dto.setUserId(order.getUser());

        OrderDetail orderDetail = orderDetailRepository.findByOrder(order)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_DETAIL_NOT_FOUND));

        dto.setOrderCount(orderDetail.getOrderCount());
        dto.setOrderAmount(orderDetail.getOrderAmount());
        dto.setRefundStatus(order.isRefundStatus());
        // LocalDateTime을 Instant로 변환 ZoneId.systemDefault(): 현재 시스템의 기본 시간대를 가져옴
        dto.setRegDate(order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
        // 주문 날짜 추가
        dto.setOrderStatus(order.getOrderStatus());  // 주문 상태 추가
        return dto;
    }

}
