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
public class ReviewServiceImpl implements ReviewService {

    // (의존성 주입) 서비스는 레포지토리에게 일을 시켜야 함
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository; // 강좌 ID로 강좌를 찾아야 함!

    private IllegalArgumentException courseNotFound(Long courseId) {
        return new IllegalArgumentException("해당 강좌를 찾을 수 없습니다. id=" + courseId);
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용으로 읽기에 성능 최적화
    public Page<ReviewResponseDTO> getReviewsByCourse(Long courseId, Pageable pageable) {

        // courseId로 강좌를 조회
        // Optional을 반환하므로, 존재하지 않으면 예외를 발생.
        Course course =
                courseRepository.findById(courseId)
                        .orElseThrow(() -> courseNotFound(courseId)); // 없는 경우 IllegalArgumentException 발생

        // 조회된 Course 엔티티를 기반으로 리뷰를 페이지 단위로 조회
        // Pageable 객체에는 페이지 번호, 크기, 정렬 정보가 들어있음
        Page<Review> reviewPage = reviewRepository.findByCourse(course, pageable);

        // Page<Review>를 Page<ReviewResponseDTO>로 변환
        // .map()을 사용하면 Page 내부의 각 Review 객체를 ReviewResponseDTO로 변환
        return reviewPage.map(ReviewResponseDTO::new);
    }

}
