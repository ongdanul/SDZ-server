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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    @Value("${file.upload-dir}")
    String uploadsDir;

//    String uploadsDir = "src/main/resources/static/uploads/";

    @Transactional
    public String uploadImage(Product product, List<MultipartFile> images, MultipartFile thumbnail) {
        String thumbnailPath = null; // 썸네일 경로를 저장할 변수

        for (MultipartFile image : images) {
            try {
                // 이미지 파일 경로를 저장
                String dbFilePath = saveImage(image);

                // 썸네일로 지정된 이미지와 비교
                if (thumbnail != null && image.getOriginalFilename().equals(thumbnail.getOriginalFilename())) {
                    thumbnailPath = dbFilePath; // 썸네일 경로 설정
                }

                // image 엔티티 생성 및 저장
                Image newImage = new Image(product, dbFilePath);
                imageRepository.save(newImage);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }

        // 썸네일 경로가 설정되지 않았을 경우 예외 처리
        if (thumbnailPath == null && thumbnail != null) {
            throw new CustomException(ErrorCode.THUMBNAIL_NOT_FOUND);
        }

        return thumbnailPath; // 썸네일 경로 반환
    }

    @Transactional
    public String saveImage(MultipartFile image) throws IOException {
        // 파일 이름 생성
        String fileName = UUID.randomUUID().toString().replace("-", "");
        // 실제 파일이 저장될 경로
        String filePath = uploadsDir + fileName;
        // DB에 저장할 경로 문자열
        String dbFilePath = "/uploads/" + fileName;

        Path path = Paths.get(filePath); // Path 객체 생성
        Files.createDirectories(path.getParent()); // 디렉토리 생성
        Files.write(path, image.getBytes()); // 디렉토리에 파일 저장

        return dbFilePath;
    }

    @Transactional
    public void deleteImage(Image image) {
        String localFilePath = "/home/kdt/backend" + image.getImagePath();
        deleteLocalFile(localFilePath);
        imageRepository.delete(image);
    }

    @Transactional
    public void deleteLocalFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_DELETE_FAILED);
        }
    }
}