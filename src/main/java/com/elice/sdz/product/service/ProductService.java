package com.elice.sdz.product.service;

import com.elice.sdz.global.exception.product.OutOfStockException;
import com.elice.sdz.global.exception.product.ProductNotFoundException;
import com.elice.sdz.product.dto.ProductDTO;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {


    private final ProductRepository productRepository;

    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product(
                null,
                productDTO.getProductName(),
                productDTO.getProductCount(),
                productDTO.getProductAmount(),
                productDTO.getProductContent()
        );
        return productRepository.save(product);
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
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
            throw new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId);
        }
        productRepository.deleteById(productId);
    }
}
