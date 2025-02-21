package com.elice.sdz.product.service;

import com.elice.sdz.category.entity.Category;
import com.elice.sdz.category.repository.CategoryRepository;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.image.entity.Image;
import com.elice.sdz.image.service.ImageService;
import com.elice.sdz.orderItem.entity.OrderItemDetail;
import com.elice.sdz.orderItem.repository.OrderItemDetailRepository;
import com.elice.sdz.product.dto.ProductDTO;
import com.elice.sdz.product.dto.ProductResponseDTO;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;

import com.elice.sdz.user.dto.request.PageRequestDTO;
import com.elice.sdz.user.dto.response.PageResponseDTO;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final OrderItemDetailRepository orderItemDetailRepository;

    // Product 생성
    @Transactional
    public ProductResponseDTO createProduct(ProductDTO productDTO, List<MultipartFile> images, MultipartFile thumbnail)
            throws IOException {
        // Category와 User를 ID로 받아와 조회
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Users user = userRepository.findById(productDTO.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Product 객체 생성
        Product product = new Product(
                null,  // productId는 null로 설정 (자동 생성)
                category,
                user,
                productDTO.getProductName(),
                productDTO.getProductCount(),
                productDTO.getProductAmount(),
                productDTO.getProductContent(),
                null
        );

        product = productRepository.save(product);

        // 이미지 업로드 및 썸네일 경로 설정
        String thumbnailPath = imageService.uploadImage(product, images, thumbnail);
        product.setThumbnailPath(thumbnailPath);

        productRepository.save(product); // 업데이트된 product 저장

        return product.toResponseDTO();
    }

    // Product 조회
    @Transactional(readOnly = true)
    public ProductResponseDTO getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return product.toResponseDTO();
    }

    // 모든 Product 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<ProductResponseDTO> getAllProducts(PageRequestDTO pageRequestDTO) {
        // Pageable 객체 생성, 정렬 필드 지정
        Pageable pageable = pageRequestDTO.getPageable("productId", "createdAt");
        String keyword = pageRequestDTO.getKeyword();

        // 검색어가 포함된 상품만 조회
        Page<Product> result = productRepository.findAllByKeyword(keyword, pageable);

        // PageResponseDTO 객체 생성
        List<ProductResponseDTO> dtoList = result.getContent()
                .stream()
                .map(Product::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ProductResponseDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .keyword(keyword) // 검색어 정보도 포함
                .build();
    }

    // Product 수정
    @Transactional
    public ProductResponseDTO updateProduct(
            Long productId,
            ProductDTO productDTO,
            List<MultipartFile> newImages,
            List<String> deletedImagePaths,
            String newThumbnail) throws IOException {

        // 1. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 2. 상품 정보 업데이트
        product.setProductName(productDTO.getProductName());
        product.setProductAmount(productDTO.getProductAmount());
        product.setProductCount(productDTO.getProductCount());
        product.setProductContent(productDTO.getProductContent());
        product.setCategory(category);


        // 3. 삭제할 이미지 처리
        if (deletedImagePaths != null) {
            List<Image> imagesToDelete = product.getImages().stream()
                    .filter(image -> deletedImagePaths.contains(image.getImagePath()))
                    .collect(Collectors.toList());
            for (Image image : imagesToDelete) {
                imageService.deleteImage(image); // 로컬 파일 및 DB에서 삭제
                product.getImages().remove(image); // Product 엔티티에서 이미지 삭제
            }
        }

        // 4. 썸네일 변경 처리
        if (newThumbnail != null) {
            product.setThumbnailPath(newThumbnail); // 새로운 썸네일 경로 설정
        }

        // 5. 새로운 이미지 추가
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile newImage : newImages) {
                String imagePath = imageService.saveImage(newImage);
                Image imageEntity = new Image(product, imagePath);
                product.getImages().add(imageEntity);
            }
        }

        // 6. 해당 상품을 담고있는 장바구니의 가격 정보 업데이트
        updateOrderItemDetails(product);

        // 7. 저장 및 반환
        return productRepository.save(product).toResponseDTO();
    }




    @Transactional
    public void deleteProduct(Long productId) {
        // 1. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 2. 관련 이미지 삭제
        List<Image> images = product.getImages(); // 상품과 연관된 이미지 목록
        for (Image image : images) {
            imageService.deleteImage(image); // 로컬 파일 및 DB에서 이미지 삭제
        }

        // 3. 상품 삭제
        productRepository.delete(product);
    }


    // 특정 카테고리의 Product 조회
    @Transactional(readOnly = true)
    public PageResponseDTO<ProductResponseDTO> getProductsByCategory(Long categoryId, PageRequestDTO pageRequestDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Pageable pageable = pageRequestDTO.getPageable("productId", "createdAt");
        Page<Product> result;

        if (category.getParentId() == null) {
            // 루트 카테고리인 경우 서브 카테고리 상품까지 조회
            List<Category> subCategories = categoryRepository.findByParentId(categoryId);
            subCategories.add(category);

            result = productRepository.findByCategoryIn(subCategories, pageable);

            // 루트 카테고리와 서브 카테고리에 상품이 없을 경우
            if (result.isEmpty()) {
                result = productRepository.findByCategory(category, pageable);
            }
        } else {
            // 서버 카테고리일 경우 서브 카테고리만 조회
            result = productRepository.findByCategory(category, pageable);
        }

        // PageResponseDTO 객체 생성
        List<ProductResponseDTO> dtoList = result.getContent()
                .stream()
                .map(Product::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ProductResponseDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }


    // 장바구니 아이템의 가격 업데이트
    private void updateOrderItemDetails(Product product) {
        List<OrderItemDetail> orderItemDetails = orderItemDetailRepository.findByProduct(product);

        for (OrderItemDetail detail : orderItemDetails) {
            detail.setProductAmount(product.getProductAmount()); // 상품 가격 업데이트
            orderItemDetailRepository.save(detail); // 변경 사항 저장
        }
    }
}
