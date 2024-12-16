package com.elice.sdz.product.service;

import com.elice.sdz.category.entity.Category;
import com.elice.sdz.category.repository.CategoryRepository;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.product.dto.ProductDTO;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Product createProduct(ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // Product 객체 생성
        Product product = new Product(
                null,  // productId는 null로 설정 (자동 생성)
                category,  // categoryId를 Category 객체로 설정
                //userId,  // userId를 Users 객체로 설정
                productDTO.getProductName(),
                productDTO.getProductCount(),
                productDTO.getProductAmount(),
                productDTO.getProductContent()
        );

        // Product 저장
        return productRepository.save(product);
    }


    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(Long productId, ProductDTO productDTO) {
        Product product = getProduct(productId);

        product.setProductName(productDTO.getProductName());
        product.setProductCount(productDTO.getProductCount());
        product.setProductAmount(productDTO.getProductAmount());
        product.setProductContent(productDTO.getProductContent());

        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(productId);
    }
}
