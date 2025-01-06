package com.elice.sdz.order.service;

import com.elice.sdz.delivery.dto.DeliveryAddressDTO;
import com.elice.sdz.delivery.entity.Delivery;
import com.elice.sdz.delivery.entity.DeliveryAddress;
import com.elice.sdz.delivery.repository.DeliveryAddressRepository;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.order.dto.OrderReqDto;
import com.elice.sdz.order.dto.OrderResDto;
import com.elice.sdz.order.entity.Order;
import com.elice.sdz.order.entity.OrderDetail;
import com.elice.sdz.order.repository.OrderDetailRepository;
import com.elice.sdz.order.repository.OrderRepository;
import com.elice.sdz.orderItem.dto.OrderItemDTO;
import com.elice.sdz.orderItem.repository.OrderItemRepository;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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


    //사용자 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderResDto> getOrdersByUserId(String userId) {
        return orderRepository.findByUserEmail(userId).stream().map(this::toResDto).collect(Collectors.toList());
    }
    //특정(주문Id) 주문 상세 조회
    @Transactional(readOnly = true)
    public OrderResDto findOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        return toResDto(order);
    }

    //사용자 주문 추가
    @Transactional
    public OrderResDto createOrder(OrderReqDto orderReqDto, String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (orderReqDto.getOrderItems() == null || orderReqDto.getOrderItems().isEmpty()) {
            throw new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }
        // 주문 세부 정보 리스트 생성
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderItemDTO orderItem : orderReqDto.getOrderItems()) {
            for (OrderItemDTO.OrderItemDetailDTO orderItemDetail : orderItem.getOrderItemDetails()) {
                int quantity = orderItemDetail.getQuantity();
                Product product = productRepository.findById(orderItemDetail.getProductId())
                        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

                if (product.getProductCount() < quantity) {
                    throw new CustomException(ErrorCode.OUT_OF_STOCK);  // 재고 부족 예외
                }
                // 재고 감소
                product.setProductCount(product.getProductCount() - quantity);
                productRepository.save(product);  // 변경된 상품 정보 저장

                OrderDetail orderDetail = OrderDetail.builder()
                        .orderCount(quantity)
                        .orderAmount(orderItemDetail.getProductAmount()) // 상품 가격
                        .product(product)
                        .build();
                orderDetails.add(orderDetail);
            }

        }

        DeliveryAddress deliveryAddress;
        if (orderReqDto.isNewAddress()) {
            deliveryAddress = new DeliveryAddress();
            deliveryAddress.setUser(user);
            deliveryAddress.setDeliveryAddressId(orderReqDto.getDeliveryAddress().getDeliveryAddressId());
            deliveryAddress.setDeliveryAddress1(orderReqDto.getDeliveryAddress().getDeliveryAddress1());
            deliveryAddress.setDeliveryAddress2(orderReqDto.getDeliveryAddress().getDeliveryAddress2());
            deliveryAddress.setDeliveryAddress3(orderReqDto.getDeliveryAddress().getDeliveryAddress3());
            deliveryAddress.setReceiverName(orderReqDto.getDeliveryAddress().getReceiverName());
            deliveryAddress.setReceiverContact(orderReqDto.getDeliveryAddress().getReceiverContact());
            deliveryAddress.setDeliveryRequest(orderReqDto.getDeliveryAddress().getDeliveryRequest());
            deliveryAddress.setDefaultCheck(false);

            deliveryAddressRepository.save(deliveryAddress);
        } else {
            deliveryAddress = deliveryAddressRepository.findById(orderReqDto.getDeliveryAddress().getDeliveryAddressId())
                    .orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

            if (orderReqDto.isAddressModified()) {
                deliveryAddress.setDeliveryAddress1(orderReqDto.getDeliveryAddress().getDeliveryAddress1());
                deliveryAddress.setDeliveryAddress2(orderReqDto.getDeliveryAddress().getDeliveryAddress2());
                deliveryAddress.setDeliveryAddress3(orderReqDto.getDeliveryAddress().getDeliveryAddress3());
                deliveryAddress.setReceiverName(orderReqDto.getDeliveryAddress().getReceiverName());
                deliveryAddress.setReceiverContact(orderReqDto.getDeliveryAddress().getReceiverContact());
                deliveryAddress.setDeliveryRequest(orderReqDto.getDeliveryAddress().getDeliveryRequest());
                deliveryAddressRepository.save(deliveryAddress);
            }
        }

        // 주문 객체 생성
        Order order = Order.builder()
                .user(user)
                .totalPrice(orderReqDto.getTotalPrice())
                .refundStatus(orderReqDto.isRefundStatus())
                .orderStatus(Order.Status.PAYMENTPROCESSED)
                .orderDetails(orderDetails)
                .regDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                .build();

        // 배송 정보 설정
        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryAddress(deliveryAddress);
        delivery.setDeliveryStatus(Delivery.Status.PENDING);
        order.setDelivery(delivery);

        // 주문 세부 정보와 주문 연결
        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order);
        }
        // 주문 저장
        Order savedOrder = orderRepository.save(order);
        return toResDto(savedOrder);
    }

    //사용자 주문 수정
    @Transactional
    public OrderResDto updateOrder(Long id, OrderReqDto orderReqDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getOrderStatus() != Order.Status.PENDING) {
            throw new CustomException(ErrorCode.ORDER_CANNOT_BE_MODIFIED);
        }

        OrderDetail orderDetail = orderDetailRepository.findByOrder(order)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_DETAIL_NOT_FOUND));


        order.setRefundStatus(orderReqDto.isRefundStatus());

        orderDetailRepository.save(orderDetail);
        Order updatedOrder = orderRepository.save(order);
        return toResDto(updatedOrder);
    }

    //사용자 주문취소
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        // 주문에 연결된 OrderDetail 목록을 조회
        List<OrderDetail> orderDetails = order.getOrderDetails();

        // 각 주문 상품에 대해 재고 처리
        for (OrderDetail detail : orderDetails) {
            Product product = detail.getProduct();
            if (product != null) {
                product.setProductCount(product.getProductCount() + detail.getOrderCount());
                productRepository.save(product);
            }
        }

        orderRepository.delete(order);
    }

    //관리자 모든 주문 조회
    @Transactional(readOnly = true)
    public List<OrderResDto> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResDto)
                .collect(Collectors.toList());

    }

    //관리자 주문 상태수정
    @Transactional
    public OrderResDto updateOrderStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        order.setOrderStatus(status);
        return toResDto(orderRepository.save(order));
    }
    //관리자 주문 삭제
    @Transactional
    public void deleteOrderByAdmin(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        // 주문에 연결된 OrderDetail 목록을 조회
        List<OrderDetail> orderDetails = order.getOrderDetails();

        // 각 주문 상품에 대해 재고 처리
        for (OrderDetail detail : orderDetails) {
            Product product = detail.getProduct();
            if (product != null) {
                product.setProductCount(product.getProductCount() + detail.getOrderCount());
                productRepository.save(product);
            }
        }

        orderRepository.delete(order);
    }


    //Entity->toDto 변환
    private OrderResDto toResDto(Order order) {
        OrderResDto dto = new OrderResDto();



        // 기본 필드 매핑
        dto.setOrderId(order.getOrderId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setRefundStatus(order.isRefundStatus());
        dto.setRegDate(order.getRegDate().substring(0, 10).replace("-", "."));
        dto.setOrderStatus(order.getOrderStatus());
        dto.setEmail(order.getUser().getEmail());

        if (order.getUser() != null) {
            dto.setEmail(order.getUser().getEmail());
        }
        // OrderItemDTO 리스트 생성
        List<OrderItemDTO> orderItems = order.getOrderDetails().stream()
                .map(this::createOrderItemDTO)
                .collect(Collectors.toList());
        dto.setOrderItems(orderItems);

        if (order.getDelivery() != null && order.getDelivery().getDeliveryAddress() != null) {
            dto.setDeliveryAddress(toDeliveryAddressDto(order.getDelivery().getDeliveryAddress()));
        }
        return dto;
    }
    private DeliveryAddressDTO toDeliveryAddressDto(DeliveryAddress deliveryAddress) {
        return DeliveryAddressDTO.builder()
                .deliveryAddressId(deliveryAddress.getDeliveryAddressId())
                .deliveryAddress1(deliveryAddress.getDeliveryAddress1())
                .deliveryAddress2(deliveryAddress.getDeliveryAddress2())
                .deliveryAddress3(deliveryAddress.getDeliveryAddress3())
                .receiverName(deliveryAddress.getReceiverName())
                .receiverContact(deliveryAddress.getReceiverContact())
                .deliveryRequest(deliveryAddress.getDeliveryRequest())
                .build();
    }

    private OrderItemDTO createOrderItemDTO(OrderDetail orderDetail) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();

        // OrderItemDTO의 orderItemDetails 리스트 생성
        List<OrderItemDTO.OrderItemDetailDTO> orderItemDetails = new ArrayList<>();

        // OrderDetail에서 OrderItemDetailDTO로 변환
        OrderItemDTO.OrderItemDetailDTO orderItemDetailDTO = new OrderItemDTO.OrderItemDetailDTO();
        if (orderDetail.getProduct() != null) {
            orderItemDetailDTO.setProductId(orderDetail.getProduct().getProductId());  // 제품 ID
            orderItemDetailDTO.setProductName(orderDetail.getProduct().getProductName());  // 제품 이름
            orderItemDetailDTO.setThumbnailPath(orderDetail.getProduct().getThumbnailPath());  // 썸네일 경로
        }

        orderItemDetailDTO.setQuantity(orderDetail.getOrderCount());  // 주문 수량
        orderItemDetailDTO.setProductAmount(orderDetail.getOrderAmount());  // 주문 금액
        // 변환된 OrderItemDetailDTO를 orderItemDetails 리스트에 추가
        orderItemDetails.add(orderItemDetailDTO);

        // 변환된 orderItemDetails를 OrderItemDTO에 설정
        orderItemDTO.setOrderItemDetails(orderItemDetails);

        return orderItemDTO;
    }

}
