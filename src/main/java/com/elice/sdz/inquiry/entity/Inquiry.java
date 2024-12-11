package com.elice.sdz.inquiry.entity;

import com.elice.sdz.image.entity.Image;
import com.elice.sdz.product.entity.Product;
import com.elice.sdz.user.entity.Users;
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
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @Column(name = "inquiry_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users userId;

    @Column(name = "inquiry_type", length = 20, nullable = false)
    private String inquiryType;

    @Column(name = "inquiry_title", length = 50, nullable = false)
    private String inquiryTitle;

    @Column(name = "inquiry_content", length = 500, nullable = false)
    private String inquiryContent;

    @Column(name = "reg_date", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant regDate;

    @Column(name = "answer_content", length = 500)
    private String answerContent;

    @Column(name = "answer_reg_date",
            columnDefinition = "TIMESTAMP DEFAULT NULL")
    private Instant answerRegDate;

    @Column(name = "inquiry_secret", nullable = false,
            columnDefinition = "BIT(1) DEFAULT 0")
    private boolean inquirySecret;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_status", nullable = false)
    private Status inquiryStatus;

    @OneToMany(mappedBy = "inquiryId", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Image> images = new ArrayList<>();

    public enum Status {
        PENDING,
        ANSWERED;
    }
}
