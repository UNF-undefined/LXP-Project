package com.example.projectlxp.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.global.dto.PageDTO;
import com.example.projectlxp.global.dto.PageResponse;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.review.dto.ReviewRequestDTO;
import com.example.projectlxp.review.dto.ReviewResponseDTO;
import com.example.projectlxp.review.entity.Review;
import com.example.projectlxp.review.repository.ReviewRepository;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;
import com.example.projectlxp.util.ProfanityFilter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // "final이 붙은 필드의 생성자를 자동으로 만들어 줌(생성자 주입)"
@Transactional(readOnly = true) // 조회 전용으로 읽기에 성능 최적화
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProfanityFilter profanityFilter;

    @Override
    public PageResponse<List<ReviewResponseDTO>> getReviewsByCourse(
            Long courseId, Pageable pageable) {
        Course course = this.getCourseOrThrow(courseId);

        Page<Review> reviewPage = reviewRepository.findByCourse(course, pageable);
        Page<ReviewResponseDTO> dtoPage = reviewPage.map(ReviewResponseDTO::of);

        PageDTO pageInfo = PageDTO.of(dtoPage);
        return PageResponse.success(dtoPage.getContent(), pageInfo);
    }

    /** '리뷰 작성' 메서드 */
    @Transactional
    @Override
    public ReviewResponseDTO createReview(Long courseId, ReviewRequestDTO requestDTO, Long userId) {

        User user = this.getUserOrThrow(userId);
        Course course = this.getCourseOrThrow(courseId);

        this.validateReviewCreation(user, course);

        String cleanContent = profanityFilter.filter(requestDTO.getContent());

        Review newReview =
                Review.builder()
                        .content(cleanContent)
                        .rating(requestDTO.getRating())
                        .user(user)
                        .course(course)
                        .build();

        Review savedReview = reviewRepository.save(newReview);

        return ReviewResponseDTO.of(savedReview, user.getName());
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId, Long userId) {

        User user = this.getUserOrThrow(userId);
        Review review = this.getReviewOrThrow(reviewId);

        this.checkReviewOwner(review, user);

        reviewRepository.delete(review);
    }

    @Transactional
    @Override
    public ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO requestDTO, Long userId) {

        User user = this.getUserOrThrow(userId);
        Review review = this.getReviewOrThrow(reviewId);

        this.checkReviewOwner(review, user);

        String cleanContent = profanityFilter.filter(requestDTO.getContent());

        review.updateReview(cleanContent, requestDTO.getRating());

        return ReviewResponseDTO.of(review, user.getName());
    }

    private void checkReviewOwner(Review review, User user) {
        Long reviewOwnerId = review.getUser().getId();
        if (!reviewOwnerId.equals(user.getId())) {

            throw new RuntimeException("해당 리뷰에 대한 권한이 없습니다.");
        }
    }

    private Course getCourseOrThrow(Long courseId) {
        return courseRepository
                .findById(courseId)
                .orElseThrow(() -> new CustomBusinessException("해당 강좌를 찾을 수 없습니다. id=" + courseId));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomBusinessException("해당 유저를 찾을 수 없습니다. id=" + userId));
    }

    private Review getReviewOrThrow(Long reviewId) {
        return reviewRepository
                .findById(reviewId)
                .orElseThrow(() -> new CustomBusinessException("해당 리뷰를 찾을 수 없습니다. id=" + reviewId));
    }

    private void validateReviewCreation(User user, Course course) {
        // 검증 1 (수강생 자격)
        if (!enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new CustomBusinessException("이 강좌를 수강한 학생만 리뷰를 작성할 수 있습니다.");
        }

        // 검증 2 (중복 작성)
        if (reviewRepository.existsByUserAndCourse(user, course)) {
            throw new CustomBusinessException("이미 이 강좌에 대한 리뷰를 작성했습니다.");
        }
    }
}
