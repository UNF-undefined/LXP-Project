package com.example.projectlxp.review.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.global.dto.BaseResponse;
import com.example.projectlxp.global.dto.PageResponse;
import com.example.projectlxp.review.dto.ReviewRequestDTO;
import com.example.projectlxp.review.dto.ReviewResponseDTO;
import com.example.projectlxp.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    // 강좌 별 리뷰 조회
    @GetMapping("/courses/{courseId}")
    public PageResponse<ReviewResponseDTO> getReviewsByCourse(
            @PathVariable Long courseId, Pageable pageable) {

        return reviewService.getReviewsByCourse(courseId, pageable);
    }

    // 리뷰 작성
    @PostMapping("/courses/{courseId}")
    public BaseResponse<ReviewResponseDTO> createReview(
            @PathVariable Long courseId,
            @Valid @RequestBody ReviewRequestDTO requestDTO,

            // Security 대신 '1번 유저'가 썼다고 가정하고 임시 ID를 받음
            // (나중에 Security 적용되면 이 파라미터는 @AuthenticationPrincipal User user로 변경됨)
            @RequestParam(defaultValue = "1") Long tempUserId) {

        ReviewResponseDTO responseDTO =
                reviewService.createReview(courseId, requestDTO, tempUserId);

        return BaseResponse.success(responseDTO);
    }
}
