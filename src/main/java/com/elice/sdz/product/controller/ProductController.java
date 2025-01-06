package com.elice.sdz.product.controller;

import com.elice.sdz.product.dto.ProductDTO;
import com.elice.sdz.product.dto.ProductResponseDTO;
import com.elice.sdz.product.service.ProductService;
import com.elice.sdz.user.dto.request.PageRequestDTO;
import com.elice.sdz.user.dto.response.PageResponseDTO;
import com.elice.sdz.user.service.AuthenticationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final AuthenticationService authenticationService;

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> getAllProducts(@ParameterObject PageRequestDTO pageRequestDTO) {
        try {
            PageResponseDTO<ProductResponseDTO> products = productService.getAllProducts(pageRequestDTO);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestPart("productDTO") @Valid String productDTOJson,
                                                            @RequestPart("images") List<MultipartFile> images,
                                                            @RequestPart("thumbnail") MultipartFile thumbnail) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO productDTO = objectMapper.readValue(productDTOJson, ProductDTO.class);
            productDTO.setUserId(authenticationService.getCurrentUser());
            ProductResponseDTO productResponseDTO = productService.createProduct(productDTO, images, thumbnail);
            return new ResponseEntity<>(productResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long productId,
            @RequestPart("productDTO") @Valid String productDTOJson,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "deletedImagePaths", required = false) String deletedImagePathsJson,
            @RequestPart(value = "newThumbnail", required = false) String newThumbnail) {
        try {
            // JSON 문자열을 ProductDTO 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO productDTO = objectMapper.readValue(productDTOJson, ProductDTO.class);
            productDTO.setUserId(authenticationService.getCurrentUser());
            // deletedImagePaths JSON 파싱
            List<String> deletedImagePaths = objectMapper.readValue(
                    deletedImagePathsJson, new TypeReference<List<String>>() {});

            // 서비스 호출
            ProductResponseDTO productResponseDTO = productService.updateProduct(
                    productId, productDTO, newImages, deletedImagePaths, newThumbnail);

            return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @ParameterObject PageRequestDTO pageRequestDTO
    ) {
        try {
            PageResponseDTO<ProductResponseDTO> products = productService.getProductsByCategory(categoryId, pageRequestDTO);

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
