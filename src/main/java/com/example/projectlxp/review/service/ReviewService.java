package com.example.projectlxp.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.projectlxp.review.dto.ReviewResponseDTO;

public interface ReviewService {
    /**
     * 특정 강좌의 리뷰 목록을 페이징하여 조회. Interface에는 이 메서드 껍데기만 존재
     *
     * @param courseId 조회할 강좌의 ID
     * @param pageable 페이징 및 정렬 정보 (page, size, sort)
     * @return 페이징 처리된 리뷰 DTO 목록 (Page<ReviewResponseDto>)
     */
    Page<ReviewResponseDTO> getReviewsByCourse(Long courseId, Pageable pageable);
}
