package com.elice.sdz.product.entity;

import com.elice.sdz.category.entity.Category;
import com.elice.sdz.image.entity.Image;
import com.elice.sdz.inquiry.entity.Inquiry;
import com.elice.sdz.review.entity.Review;
import com.elice.sdz.user.entity.Users;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;

    //@ManyToOne
    //@JsonBackReference
    //@JoinColumn(name = "user_id", nullable = false)
    //private Users userId;

    @Column(name = "product_name", length = 50, nullable = false)
    private String productName;

    @Column(name = "product_count", nullable = false)
    private int productCount;

    @Column(name = "product_amount", nullable = false)
    private Double productAmount;

    @Column(name = "product_content", length = 3000, nullable = false)
    private String productContent;

    @Column(name = "reg_date", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant regDate;

    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Review> reviews  = new ArrayList<>();

    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Inquiry> inquiries = new ArrayList<>();

    public Product(Long productId ,Category categoryId,/* Users userId,*/ String productName, int productCount,
                   Double productAmount, String productContent) {
        this.productId = productId;
        this.categoryId = categoryId;
        //this.userId = userId;
        this.productName = productName;
        this.productCount = productCount;
        this.productAmount = productAmount;
        this.productContent = productContent;
    }

}
