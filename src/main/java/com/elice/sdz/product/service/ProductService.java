package com.elice.sdz.product.service;

import com.elice.sdz.category.entity.Category;
import com.elice.sdz.category.repository.CategoryRepository;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.product.dto.ProductDTO;
import com.elice.sdz.product.dto.ProductResponseDTO;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;

import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Product 생성
    public ProductResponseDTO createProduct(ProductDTO productDTO) {
        // Category와 User를 ID로 받아와 조회
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Users user = userRepository.findById("admin123")
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Product 객체 생성
        Product product = new Product(
                null,  // productId는 null로 설정 (자동 생성)
                category,
                user,
                productDTO.getProductName(),
                productDTO.getProductCount(),
                productDTO.getProductAmount(),
                productDTO.getProductContent()
        );

        return productRepository.save(product).toResponseDTO();
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

    // Product 삭제
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    }
}
