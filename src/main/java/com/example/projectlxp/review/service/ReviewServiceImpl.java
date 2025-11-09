package com.example.projectlxp.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.review.dto.ReviewResponseDTO;
import com.example.projectlxp.review.entity.Review;
import com.example.projectlxp.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // "final이 붙은 필드의 생성자를 자동으로 만들어 줌(생성자 주입)"
@Transactional(readOnly = true) // 이 클래스의 모든 메서드는 기본적으로 '조회 전용' readOnly = true로 성능 최적화
public class ReviewServiceImpl implements ReviewService {

    // (의존성 주입) 서비스는 레포지토리에게 일을 시켜야 함
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository; // 강좌 ID로 강좌를 찾아야 함!

    private IllegalArgumentException courseNotFound(Long courseId) {
        return new IllegalArgumentException("해당 강좌를 찾을 수 없습니다. id=" + courseId);
    }

    @Override
    public Page<ReviewResponseDTO> getReviewsByCourse(Long courseId, Pageable pageable) {

        // Controller는 courseId만 준다.
        // Repository에게는 Course(엔티티)를 넘겨줘야 함 그래서 CourseRepository로 먼저 강좌를 찾는다.
        // 만약 ID에 해당하는 강좌가 없으면, 예외를 발생. (데이터 정합성)
        Course course =
                courseRepository
                        .findById(courseId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 강좌를 찾을 수 없습니다. id=" + courseId));

        // reviewRepository 메서드 호출 (Course 엔티티와 Pageable 객체를 전달)
        Page<Review> reviewPage = reviewRepository.findByCourse(course, pageable);

        // Page<Review> (엔티티 페이지)를 Page<ReviewResponseDto> (DTO 페이지)로 변환
        // .map()을 사용하면, 리스트의 각 Review 객체를 ReviewResponseDto 생성자에 넣어 새 DTO로 변환.
        Page<ReviewResponseDTO> dtoPage = reviewPage.map(review -> new ReviewResponseDTO(review));

        // 변환된 DTO 페이지를 Controller에게 반환
        return dtoPage;
    }

}
