package com.elice.sdz.product.service;

import com.elice.sdz.product.dto.ProductDTO;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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
        Optional<Product> product = productRepository.findById(productId);
        return product.orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(Long productId, ProductDTO productDTO) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setProductName(productDTO.getProductName());
            product.setProductCount(productDTO.getProductCount());
            product.setProductAmount(productDTO.getProductAmount());
            product.setProductContent(productDTO.getProductContent());
            return productRepository.save(product);
        }
        return null;
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}