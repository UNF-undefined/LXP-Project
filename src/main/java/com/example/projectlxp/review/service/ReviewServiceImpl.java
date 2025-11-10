package com.example.projectlxp.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.global.dto.PageDTO;
import com.example.projectlxp.global.dto.PageResponse;
import com.example.projectlxp.review.dto.ReviewRequestDTO;
import com.example.projectlxp.review.dto.ReviewResponseDTO;
import com.example.projectlxp.review.entity.Review;
import com.example.projectlxp.review.repository.ReviewRepository;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // "final이 붙은 필드의 생성자를 자동으로 만들어 줌(생성자 주입)"
@Transactional(readOnly = true) // 조회 전용으로 읽기에 성능 최적화
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public PageResponse<ReviewResponseDTO> getReviewsByCourse(Long courseId, Pageable pageable) {
        Course course =
                courseRepository
                        .findById(courseId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 강좌를 찾을 수 없습니다. id=" + courseId));

        Page<Review> reviewPage = reviewRepository.findByCourse(course, pageable);

        Page<ReviewResponseDTO> dtoPage = reviewPage.map(ReviewResponseDTO::new);

        PageDTO pageInfo = PageDTO.of(dtoPage);

        return PageResponse.success((ReviewResponseDTO) dtoPage.getContent(), pageInfo);
    }

    /** '리뷰 작성' 메서드 */
    @Transactional // ★ '쓰기'용 트랜잭션 (readOnly = false)
    @Override
    public ReviewResponseDTO createReview(Long courseId, ReviewRequestDTO requestDTO, Long userId) {

        // 1. 엔티티 조회
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 유저를 찾을 수 없습니다. id=" + userId));

        Course course =
                courseRepository
                        .findById(courseId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 강좌를 찾을 수 없습니다. id=" + courseId));

        boolean isEnrolled = enrollmentRepository.existsByUserAndCourse(user, course);
        if (!isEnrolled) {
            throw new RuntimeException("이 강좌를 수강한 학생만 리뷰를 작성할 수 있습니다.");
        }

        boolean hasReviewed = reviewRepository.existsByUserAndCourse(user, course);
        if (hasReviewed) {
            throw new RuntimeException("이미 이 강좌에 대한 리뷰를 작성했습니다.");
        }

        Review newReview =
                Review.builder()
                        .content(requestDTO.getContent())
                        .rating(requestDTO.getRating())
                        .user(user)
                        .course(course)
                        .build();

        Review savedReview = reviewRepository.save(newReview);

        return new ReviewResponseDTO(savedReview);
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId, Long userId) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 유저를 찾을 수 없습니다. id=" + userId));

        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 리뷰를 찾을 수 없습니다. id=" + reviewId));

        this.checkReviewOwner(review, user);

        reviewRepository.delete(review);
    }

    private void checkReviewOwner(Review review, User user) {
        Long reviewOwnerId = review.getUser().getId();
        if (!reviewOwnerId.equals(user.getId())) {

            throw new RuntimeException("해당 리뷰에 대한 권한이 없습니다.");
        }
    }
}
