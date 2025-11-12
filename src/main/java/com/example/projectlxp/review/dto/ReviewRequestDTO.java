package com.example.projectlxp.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 리뷰 작성을 위해 클라이언트로부터 요청 받는 DTO */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDTO {
    @NotBlank(message = "리뷰 내용은 필수 항목입니다.")
    private String content;

    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하이어야 합니다.")
    private double rating;
}
