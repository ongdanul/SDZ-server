package com.elice.sdz.image.entity;

import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.inquiry.entity.Inquiry;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
//@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image")
public class Image extends BaseEntity {

    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    public Image(Product product, String imagePath){
        this.product = product;
        this.imagePath = imagePath;
    }
}
