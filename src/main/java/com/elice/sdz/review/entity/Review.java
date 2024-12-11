package com.elice.sdz.review.entity;

import com.elice.sdz.image.entity.Image;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product productId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "review_content", length = 1000, nullable = false)
    private String reviewContent;

    @Column(name = "reg_date", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant regDate;

    @OneToMany(mappedBy = "reviewId", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Image> images = new ArrayList<>();
}
