package com.example.projectlxp.review.service;

// --- Junit5 & AssertJ Imports ---
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.global.dto.PageResponse;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.review.dto.ReviewRequestDTO;
import com.example.projectlxp.review.dto.ReviewResponseDTO;
import com.example.projectlxp.review.entity.Review;
import com.example.projectlxp.review.repository.ReviewRepository;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;
import com.example.projectlxp.util.ProfanityFilter;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewServiceImplTest {

    @InjectMocks private ReviewServiceImpl reviewService;

    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private ProfanityFilter mockProfanityFilter;

    @Mock private User mockUser;
    @Mock private User mockDeletedUser;
    @Mock private User mockAttacker;
    @Mock private Course mockCourse;
    @Mock private Review mockReview;
    @Mock private Review mockReviewFromDeletedUser;

    private Long userId = 1L;
    private Long deletedUserId = 2L;
    private Long attackerId = 999L;
    private Long courseId = 1L;
    private Long reviewId = 100L;

    @BeforeEach
    void setUp() {
        when(mockUser.getId()).thenReturn(userId);
        when(mockDeletedUser.getId()).thenReturn(deletedUserId);
        when(mockAttacker.getId()).thenReturn(attackerId);
        when(mockCourse.getId()).thenReturn(courseId);
        when(mockReview.getId()).thenReturn(reviewId);
        when(mockReview.getUser()).thenReturn(mockUser);
        when(mockReview.getCourse()).thenReturn(mockCourse);
        when(mockProfanityFilter.filter(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // [ 기능1: 리뷰 조회 ]
    @Test
    @DisplayName("리뷰 조회: '삭제된 유저'의 이름은 '알 수 없음'으로 반환된다")
    void getReviewsByCourse_HandlesSoftDelete() {
        // --- [ Given ] ---
        Pageable pageable = Pageable.ofSize(10);
        when(mockUser.isDeleted()).thenReturn(false);
        when(mockUser.getName()).thenReturn("정상유저");
        when(mockDeletedUser.isDeleted()).thenReturn(true);

        when(mockReview.getUser()).thenReturn(mockUser);
        when(mockReview.getContent()).thenReturn("좋은 강의!");
        when(mockReview.getRating()).thenReturn(5.0);

        when(mockReviewFromDeletedUser.getUser()).thenReturn(mockDeletedUser);
        when(mockReviewFromDeletedUser.getContent()).thenReturn("삭제된 리뷰");
        when(mockReviewFromDeletedUser.getRating()).thenReturn(3.0);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));
        List<Review> reviewList = List.of(mockReview, mockReviewFromDeletedUser);
        Page<Review> reviewPage = new PageImpl<>(reviewList, pageable, 2L);
        when(reviewRepository.findByCourse(mockCourse, pageable)).thenReturn(reviewPage);

        // --- [ When ] ---
        PageResponse<List<ReviewResponseDTO>> response =
                reviewService.getReviewsByCourse(courseId, pageable);

        // --- [ Then ] ---
        assertThat(response.getPage().getTotalElements()).isEqualTo(2);
        assertThat(response.getData()).anyMatch(dto -> dto.getUsername().equals("정상유저"));
        assertThat(response.getData()).anyMatch(dto -> dto.getUsername().equals("알 수 없음"));
    }

    // [ 기능 2: 리뷰 작성 (정상) ]
    @Test
    @DisplayName("리뷰 작성: '성공' 케이스")
    void createReview_Success() {
        // --- [ Given ] ---
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("새 리뷰 내용", 5);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));
        when(enrollmentRepository.existsByUserAndCourse(mockUser, mockCourse)).thenReturn(true);
        when(reviewRepository.existsByUserAndCourse(mockUser, mockCourse)).thenReturn(false);
        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mockUser.isDeleted()).thenReturn(false);
        when(mockUser.getName()).thenReturn("테스트유저");

        // --- [ When ] ---
        ReviewResponseDTO result = reviewService.createReview(courseId, requestDTO, userId);

        // --- [ Then ] ---
        assertThat(result.getContent()).isEqualTo("새 리뷰 내용");
        assertThat(result.getUsername()).isEqualTo("테스트유저");
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    // [ 기능 2: 리뷰 작성 (예외 1 - 중복) ]
    @Test
    @DisplayName("리뷰 작성: '중복 작성' 시, 예외가 발생한다")
    void testCreateReview_FailsOnDuplicate() {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("중복 리뷰", 5);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));
        when(enrollmentRepository.existsByUserAndCourse(mockUser, mockCourse)).thenReturn(true);
        when(reviewRepository.existsByUserAndCourse(mockUser, mockCourse)).thenReturn(true);
        assertThrows(
                CustomBusinessException.class,
                () -> {
                    reviewService.createReview(courseId, requestDTO, userId);
                });
    }

    // [ 기능 2: 리뷰 작성 (예외 2 - 비수강) ]
    @Test
    @DisplayName("리뷰 작성: '수강생이 아닌' 경우, 예외가 발생한다")
    void testCreateReview_FailsIfNotEnrolled() {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("수강 안 함", 1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));
        when(enrollmentRepository.existsByUserAndCourse(mockUser, mockCourse)).thenReturn(false);
        assertThrows(
                CustomBusinessException.class,
                () -> {
                    reviewService.createReview(courseId, requestDTO, userId);
                });
    }

    @Test
    @DisplayName("리뷰 작성: 비속어가 포함된 경우, 마스킹되어 저장/반환된다")
    void createReview_WithProfanity_ShouldBeMasked() {
        // --- [ Given ] ---
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));
        when(enrollmentRepository.existsByUserAndCourse(mockUser, mockCourse)).thenReturn(true);
        when(reviewRepository.existsByUserAndCourse(mockUser, mockCourse)).thenReturn(false);

        ReviewRequestDTO dirtyRequest = new ReviewRequestDTO("이런 바보 같은 강의", 1);

        when(mockProfanityFilter.filter("이런 바보 같은 강의")).thenReturn("이런 ** 같은 강의");

        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        when(mockUser.getName()).thenReturn("테스트유저");

        // --- [ When ] ---
        ReviewResponseDTO response = reviewService.createReview(courseId, dirtyRequest, userId);

        // --- [ Then ] ---
        assertThat(response.getContent()).isEqualTo("이런 ** 같은 강의");
        assertThat(response.getUsername()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("리뷰 작성: '숫자/특수문자' 우회 비속어 포함 시, 마스킹되어 저장된다")
    void createReview_WithObfuscatedProfanity_ShouldBeMasked() {
        // --- [ Given ] ---
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));
        when(enrollmentRepository.existsByUserAndCourse(mockUser, mockCourse)).thenReturn(true);
        when(reviewRepository.existsByUserAndCourse(mockUser, mockCourse)).thenReturn(false);

        ReviewRequestDTO dirtyRequest = new ReviewRequestDTO("이런 시1발@ 같은 강의", 1);

        when(mockProfanityFilter.filter("이런 시1발@ 같은 강의")).thenReturn("이런 ** 같은 강의");
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
        when(mockUser.getName()).thenReturn("테스트유저");

        // --- [ When ] ---
        ReviewResponseDTO response = reviewService.createReview(courseId, dirtyRequest, userId);

        // --- [ Then ] ---
        assertThat(response.getContent()).isEqualTo("이런 ** 같은 강의");
        assertThat(response.getUsername()).isEqualTo("테스트유저");

        verify(mockProfanityFilter, times(1)).filter("이런 시1발@ 같은 강의");
    }

    // [ 기능 3: 리뷰 수정 (정상) ]
    @Test
    @DisplayName("리뷰 수정: '성공' 케이스")
    void updateReview_Success() {
        // --- [ Given ] ---
        double expectedRating = 1.0;
        String expectedContent = "수정된 내용";
        ReviewRequestDTO updateRequest =
                new ReviewRequestDTO(expectedContent, (int) expectedRating);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockReview.getUser()).thenReturn(mockUser);

        doNothing().when(mockReview).updateReview(anyString(), anyDouble());

        when(mockUser.getName()).thenReturn("테스트유저");
        when(mockReview.getRating()).thenReturn(expectedRating);
        when(mockReview.getContent()).thenReturn(expectedContent);

        // --- [ When (실행) ] ---
        ReviewResponseDTO result = reviewService.updateReview(reviewId, updateRequest, userId);

        // --- [ Then (검증) ] ---
        verify(mockReview, times(1)).updateReview(expectedContent, expectedRating);

        assertThat(result.getContent()).isEqualTo(expectedContent);
        assertThat(result.getRating()).isEqualTo(expectedRating);
        assertThat(result.getUsername()).isEqualTo("테스트유저");
    }

    // [ 기능 3: 리뷰 수정 (예외 - 권한) ]
    @Test
    @DisplayName("리뷰 수정: '남의' 리뷰를 수정 시도 시, 예외가 발생한다")
    void updateReview_FailsOnPermissionDenied() {
        ReviewRequestDTO updateRequest = new ReviewRequestDTO("해킹 시도", 1);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
        when(userRepository.findById(attackerId)).thenReturn(Optional.of(mockAttacker));
        when(mockReview.getUser()).thenReturn(mockUser);

        assertThrows(
                RuntimeException.class,
                () -> {
                    reviewService.updateReview(reviewId, updateRequest, attackerId);
                });
    }

    // [ 기능 4: 리뷰 삭제 (정상) ]
    @Test
    @DisplayName("리뷰 삭제: '성공' 케이스")
    void deleteReview_Success() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockReview.getUser()).thenReturn(mockUser); // '인가' 통과
        doNothing().when(reviewRepository).delete(mockReview);

        reviewService.deleteReview(reviewId, userId);

        verify(reviewRepository, times(1)).delete(mockReview);
    }

    // [ 기능 4: 리뷰 삭제 (예외 - 권한) ]
    @Test
    @DisplayName("리뷰 삭제: '남의' 리뷰를 삭제 시도 시, '권한 없음' 예외가 발생한다")
    void testDeleteReview_FailsOnPermissionDenied() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
        when(userRepository.findById(attackerId)).thenReturn(Optional.of(mockAttacker));
        when(mockReview.getUser()).thenReturn(mockUser);

        assertThrows(
                RuntimeException.class,
                () -> {
                    reviewService.deleteReview(reviewId, attackerId);
                });
    }
}
