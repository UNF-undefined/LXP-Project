package com.example.projectlxp.review.dto;

import java.time.LocalDateTime;

import com.example.projectlxp.review.entity.Review;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewResponseDTO {
    private Long reviewId;
    private String content;
    private double rating;
    private String username;
    private LocalDateTime createdAt;

    public ReviewResponseDTO(Review review) {
        this.reviewId = review.getId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.username = (review.getUser().isDeleted()) ? "알 수 없음" : review.getUser().getName();
        this.createdAt = review.getCreatedAt();
    }
}
