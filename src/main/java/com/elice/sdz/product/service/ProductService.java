package com.elice.sdz.product.service;

import com.elice.sdz.category.entity.Category;
import com.elice.sdz.category.repository.CategoryRepository;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.image.service.ImageService;
import com.elice.sdz.product.dto.ProductDTO;
import com.elice.sdz.product.dto.ProductResponseDTO;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;

import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // Product 생성
    public ProductResponseDTO createProduct(ProductDTO productDTO, List<MultipartFile> images, MultipartFile thumbnail)
            throws IOException {
        // Category와 User를 ID로 받아와 조회
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Users user = userRepository.findById("admin@example.com")
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

        imageService.uploadImage(product, images);

        if (thumbnail != null) {
            String thumbnailPath = imageService.saveImage(thumbnail, "src/main/resources/static/uploads/");
            product.setThumbnailPath(thumbnailPath); // 썸네일 경로 설정
            productRepository.save(product); // 업데이트된 product 저장
        }
        return product.toResponseDTO();
    }

    // Product 조회
    public ProductResponseDTO getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return product.toResponseDTO();
    }

    // 모든 Product 조회
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(Product::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Product 수정
    public ProductResponseDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Users user = userRepository.findById(productDTO.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        product.setCategory(category);
        product.setUser(user);
        product.setProductName(productDTO.getProductName());
        product.setProductCount(productDTO.getProductCount());
        product.setProductAmount(productDTO.getProductAmount());
        product.setProductContent(productDTO.getProductContent());

        return productRepository.save(product).toResponseDTO();
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }



    // 특정 카테고리의 Product 조회
    public List<ProductResponseDTO> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        List<Product> products = productRepository.findByCategory(category);

        // Stream 대신 for 루프 사용
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();
        for (Product product : products) {
            productResponseDTOList.add(product.toResponseDTO());
        }

        return productResponseDTOList;
    }



}
