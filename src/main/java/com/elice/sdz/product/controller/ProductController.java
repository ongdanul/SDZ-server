package com.elice.sdz.product.controller;

import com.elice.sdz.product.dto.ProductDTO;
import com.elice.sdz.product.dto.ProductResponseDTO;
import com.elice.sdz.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // 상품 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long productId) {
        ProductResponseDTO productResponseDTO = productService.getProduct(productId);
        if (productResponseDTO != null) {
            return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 상품 생성
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        ProductResponseDTO productResponseDTO = productService.createProduct(productDTO);
        return new ResponseEntity<>(productResponseDTO, HttpStatus.CREATED);
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductDTO productDTO) {
        ProductResponseDTO productResponseDTO = productService.updateProduct(productId, productDTO);
        if (productResponseDTO != null) {
            return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDTO> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

}
