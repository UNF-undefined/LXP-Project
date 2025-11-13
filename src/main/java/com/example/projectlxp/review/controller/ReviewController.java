package com.example.projectlxp.review.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.global.dto.BaseResponse;
import com.example.projectlxp.global.dto.PageResponse;
import com.example.projectlxp.review.dto.ReviewRequestDTO;
import com.example.projectlxp.review.dto.ReviewResponseDTO;
import com.example.projectlxp.review.service.ReviewService;
import com.example.projectlxp.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    // 강좌 별 리뷰 조회
    @GetMapping("/courses/{courseId}")
    public PageResponse<List<ReviewResponseDTO>> getReviewsByCourse(
            @PathVariable Long courseId, Pageable pageable) {

        return reviewService.getReviewsByCourse(courseId, pageable);
    }

    // 리뷰 작성
    @PostMapping("/courses/{courseId}")
    public BaseResponse<ReviewResponseDTO> createReview(
            @PathVariable Long courseId,
            @Valid @RequestBody ReviewRequestDTO requestDTO,
            @AuthenticationPrincipal User user) {

        ReviewResponseDTO responseDTO =
                reviewService.createReview(courseId, requestDTO, user.getId());

        return BaseResponse.success(responseDTO);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public BaseResponse<Void> deleteReview(
            @PathVariable Long reviewId, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(reviewId, user.getId());
        return BaseResponse.success(null);
    }

    // 리뷰 수정
    @PatchMapping("/{reviewId}") // ★ 2. "@PatchMapping"
    public BaseResponse<ReviewResponseDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequestDTO requestDTO,
            @AuthenticationPrincipal User user) {

        ReviewResponseDTO responseDTO =
                reviewService.updateReview(reviewId, requestDTO, user.getId());

        return BaseResponse.success(responseDTO);
    }
}
