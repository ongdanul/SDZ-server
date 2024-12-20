package com.elice.sdz.image.service;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.image.entity.Image;
import com.elice.sdz.image.repository.ImageRepository;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.product.repository.ProductRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    // 상품 조회 메서드
    private Product findByProductId(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public void uploadImage(Product product, List<MultipartFile> images) {
        String uploadsDir = "src/main/resources/static/uploads/";
        for (MultipartFile image : images) {

            try {
                // 이미지 파일 경로를 저장
                String dbFilePath = saveImage(image, uploadsDir);

                // ProductThumbnail 엔티티 생성 및 저장
                Image newImage = new Image(product, dbFilePath);
                imageRepository.save(newImage);
            } catch (IOException e) {
            // 파일 저장 중 오류가 발생한 경우 처리
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }
    }

    private String saveImage(MultipartFile image, String uploadsDir) throws IOException {
        // 파일 이름 생성
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + image.getOriginalFilename();
        // 실제 파일이 저장될 경로
        String filePath = uploadsDir + fileName;
        // DB에 저장할 경로 문자열
        String dbFilePath = "/uploads/" + fileName;

        Path path = Paths.get(filePath); // Path 객체 생성
        Files.createDirectories(path.getParent()); // 디렉토리 생성
        Files.write(path, image.getBytes()); // 디렉토리에 파일 저장

        return dbFilePath;
    }
}
