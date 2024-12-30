package com.elice.sdz.product.entity;

import com.elice.sdz.category.entity.Category;
import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.image.entity.Image;
import com.elice.sdz.orderItem.entity.OrderItemDetail;
import com.elice.sdz.product.dto.ProductResponseDTO;
import com.elice.sdz.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "email", nullable = false)
    private Users user;

    @Column(name = "product_name", length = 50, nullable = false)
    private String productName;

    @Column(name = "product_count", nullable = false)
    private int productCount;

    @Column(name = "product_amount", nullable = false)
    private Double productAmount;

    @Column(name = "product_content", length = 3000, nullable = false)
    private String productContent;

    @Column(name = "thumbnail_path") // 썸네일 경로 필드
    private String thumbnailPath;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private final List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)//, orphanRemoval = true
    private final List<OrderItemDetail> orderItemDetails = new ArrayList<>();

    public ProductResponseDTO toResponseDTO() {
        List<String> imagePaths = images.stream()
                .map(Image::getImagePath) // Image 엔티티의 경로 가져오기
                .toList();

        return ProductResponseDTO.builder()
                .productId(productId)
                .productName(productName)
                .userName(user.getUserName())  // 사용자 이름
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .productCount(this.productCount)
                .productAmount(this.productAmount)
                .productContent(this.productContent)
                .thumbnailPath(thumbnailPath)
                .imagePaths(imagePaths)
                .build();
    }

}
