package com.example.projectlxp.review.service;

import org.springframework.data.domain.Pageable;

import com.example.projectlxp.global.dto.PageResponse;
import com.example.projectlxp.review.dto.ReviewRequestDTO;
import com.example.projectlxp.review.dto.ReviewResponseDTO;

public interface ReviewService {
    /**
     * 특정 강좌의 리뷰 목록을 페이징하여 조회. Interface에는 이 메서드 껍데기만 존재
     *
     * @param courseId 조회할 강좌의 ID
     * @param pageable 페이징 및 정렬 정보 (page, size, sort)
     * @return 페이징 처리된 리뷰 DTO 목록 (Page<ReviewResponseDto>)
     */
    PageResponse<ReviewResponseDTO> getReviewsByCourse(Long courseId, Pageable pageable);

    /**
     * 리뷰 작성.
     *
     * @param courseId 강좌 ID
     * @param requestDTO 리뷰 내용/평점이 담긴 DTO
     * @param userId (임시) 작성자 유저 ID
     * @return 생성된 리뷰의 상세 DTO
     */
    ReviewResponseDTO createReview(Long courseId, ReviewRequestDTO requestDTO, Long userId);

    /**
     * [ #37 ] 리뷰 삭제
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @param userId (임시) 삭제를 요청한 유저의 ID
     */
    void deleteReview(Long reviewId, Long userId);
}
