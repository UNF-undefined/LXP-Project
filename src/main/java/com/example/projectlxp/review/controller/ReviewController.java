package com.example.projectlxp.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.global.dto.BaseResponse;
import com.example.projectlxp.review.dto.ReviewResponseDTO;
import com.example.projectlxp.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<BaseResponse<Page<ReviewResponseDTO>>> getReviewsByCourse(
            @PathVariable Long courseId, Pageable pageable) {
        Page<ReviewResponseDTO> dtoList = reviewService.getReviewsByCourse(courseId, pageable);
        return ResponseEntity.ok(BaseResponse.success(dtoList));
    }
}
