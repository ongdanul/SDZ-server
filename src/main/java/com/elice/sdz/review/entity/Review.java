package com.elice.sdz.review.entity;

import com.elice.sdz.global.entity.BaseEntity;
import com.elice.sdz.image.entity.Image;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
//@ToString
@Entity
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "email", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "review_content", length = 1000, nullable = false)
    private String reviewContent;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Image> images = new ArrayList<>();
}
