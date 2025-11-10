package com.example.projectlxp.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/** 리뷰 작성을 위해 클라이언트로부터 요청 받는 DTO */
@Getter
@NoArgsConstructor
public class ReviewRequestDTO {
    private String content;
    private int rating;
}
