package com.example.projectlxp.course.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.category.repository.CategoryRepository;
import com.example.projectlxp.course.config.TestQueryDslConfig;
import com.example.projectlxp.course.dto.request.CourseSearchRequest;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseLevel;
import com.example.projectlxp.course.entity.CourseSortBy;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.review.entity.Review;
import com.example.projectlxp.review.repository.ReviewRepository;
import com.example.projectlxp.user.entity.Role;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

@ActiveProfiles("test")
@SpringBootTest
@Import({TestQueryDslConfig.class})
class CourseQueryRepositoryImplTest {

    @Autowired private CourseRepository courseQueryRepository;

    @Autowired private EntityManager em;

    private Course courseA;
    private Course courseB;
    private Course courseC;

    private JPAQueryFactory queryFactory;

    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Autowired private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        queryFactory = new JPAQueryFactory(em);

        User instructor =
                User.builder()
                        .name("testName")
                        .email("test1@test.com")
                        .hashedPassword("testPassword")
                        .role(Role.INSTRUCTOR)
                        .build();

        User student1 =
                User.builder()
                        .name("testName1")
                        .email("test2@test.com")
                        .hashedPassword("testPassword")
                        .role(Role.STUDENT)
                        .build();

        User student2 =
                User.builder()
                        .name("testName2")
                        .email("test3@test.com")
                        .hashedPassword("testPassword")
                        .role(Role.STUDENT)
                        .build();

        User student3 =
                User.builder()
                        .name("testName3")
                        .email("test4@test.com")
                        .hashedPassword("testPassword")
                        .role(Role.STUDENT)
                        .build();

        Category category = categoryRepository.save(Category.builder().name("프로그래밍").build());

        courseA =
                Course.builder()
                        .title("Course A")
                        .instructor(instructor)
                        .level(CourseLevel.BEGINNER)
                        .category(category)
                        .price(10000)
                        .build();
        courseB =
                Course.builder()
                        .title("Course B")
                        .instructor(instructor)
                        .level(CourseLevel.BEGINNER)
                        .category(category)
                        .price(50000)
                        .build();
        courseC =
                Course.builder()
                        .title("Course C")
                        .instructor(instructor)
                        .level(CourseLevel.BEGINNER)
                        .category(category)
                        .price(30000)
                        .build();

        Review review1 = Review.builder().course(courseA).user(student1).rating(5.0).build();
        Review review2 = Review.builder().course(courseB).user(student2).rating(3.0).build();
        Review review3 = Review.builder().course(courseB).user(student3).rating(2.0).build();

        categoryRepository.save(category);

        userRepository.save(instructor);
        userRepository.save(student1);
        userRepository.save(student2);
        userRepository.save(student3);

        courseRepository.save(courseA);
        courseRepository.save(courseB);
        courseRepository.save(courseC);

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        enrollmentRepository.save(Enrollment.builder().course(courseC).user(student1).build());
        enrollmentRepository.save(Enrollment.builder().course(courseC).user(student2).build());
        enrollmentRepository.save(Enrollment.builder().course(courseC).user(student3).build());

        enrollmentRepository.save(Enrollment.builder().course(courseB).user(student1).build());
        enrollmentRepository.save(Enrollment.builder().course(courseB).user(student2).build());

        enrollmentRepository.save(Enrollment.builder().course(courseA).user(student1).build());
        // ===================================================
    }

    @AfterEach
    void tearDown() {
        enrollmentRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Test
    void 별점_높은_순_정렬_테스트() {
        // Given
        CourseSearchRequest request = new CourseSearchRequest(null, null, CourseSortBy.RATING);
        Pageable pageable = PageRequest.of(0, 3);

        // When
        Page<Course> resultPage = courseQueryRepository.searchAll(request, pageable);

        // Then
        assertAll(
                () -> assertThat(resultPage.getContent()).hasSize(3),
                () -> assertThat(resultPage.getContent().get(0).getTitle()).isEqualTo("Course A"),
                () -> assertThat(resultPage.getContent().get(1).getTitle()).isEqualTo("Course B"),
                () -> assertThat(resultPage.getContent().get(2).getTitle()).isEqualTo("Course C"));
    }

    @Test
    void 수강생_많은_순_정렬_테스트() {
        // Given
        CourseSearchRequest request = new CourseSearchRequest(null, null, CourseSortBy.POPULARITY);
        Pageable pageable = PageRequest.of(0, 3);

        // When
        Page<Course> resultPage = courseQueryRepository.searchAll(request, pageable);

        // Then
        assertAll(
                () -> assertThat(resultPage.getContent()).hasSize(3),
                () -> assertThat(resultPage.getContent().get(0).getTitle()).isEqualTo("Course C"),
                () -> assertThat(resultPage.getContent().get(1).getTitle()).isEqualTo("Course B"),
                () -> assertThat(resultPage.getContent().get(2).getTitle()).isEqualTo("Course A"));
    }

    @Test
    void 가격_높은_순_정렬_테스트() {
        // Given
        CourseSearchRequest request = new CourseSearchRequest(null, null, CourseSortBy.PRICE_DESC);
        Pageable pageable = PageRequest.of(0, 3);

        // When
        Page<Course> resultPage = courseQueryRepository.searchAll(request, pageable);

        // Then
        assertAll(
                () -> assertThat(resultPage.getContent()).hasSize(3),
                () -> assertThat(resultPage.getContent().get(0).getTitle()).isEqualTo("Course B"),
                () -> assertThat(resultPage.getContent().get(1).getTitle()).isEqualTo("Course C"),
                () -> assertThat(resultPage.getContent().get(2).getTitle()).isEqualTo("Course A"));
    }

    @Test
    void 필터링_페이징_테스트() {
        // Given
        CourseSearchRequest request = new CourseSearchRequest(null, null, CourseSortBy.PRICE_ASC);
        Pageable pageable = PageRequest.of(1, 2);

        // When
        Page<Course> resultPage = courseQueryRepository.searchAll(request, pageable);

        // Then
        assertAll(
                () -> assertThat(resultPage.getTotalElements()).isEqualTo(3),
                () -> assertThat(resultPage.getNumber()).isEqualTo(1),
                () -> assertThat(resultPage.getContent()).hasSize(1),
                () -> assertThat(resultPage.getContent().get(0).getTitle()).isEqualTo("Course B"));
    }
}
