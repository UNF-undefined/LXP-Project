package com.example.projectlxp.review.dto;

import java.time.LocalDateTime;

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
}
