package com.example.projectlxp.review.dto;

import java.time.LocalDateTime;

import com.example.projectlxp.review.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    private Long reviewId;
    private String content;
    private double rating;
    private String username;
    private LocalDateTime createdAt;

    public static ReviewResponseDTO of(Review review) {
        String username =
                (review.getUser() == null || review.getUser().isDeleted())
                        ? "알 수 없음"
                        : review.getUser().getName();

        return ReviewResponseDTO.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .username(username)
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static ReviewResponseDTO of(Review review, String username) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .username(username)
                .createdAt(review.getCreatedAt())
                .build();
    }
}
