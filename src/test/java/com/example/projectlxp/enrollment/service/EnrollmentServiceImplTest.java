package com.example.projectlxp.enrollment.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.category.repository.CategoryRepository;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseLevel;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.enrollment.dto.EnrollmentResponseDTO;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.user.entity.Role;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@ActiveProfiles("test")
@SpringBootTest
class EnrollmentServiceImplTest {
    @Autowired private EnrollmentService enrollmentService;
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private CategoryRepository categoryRepository;

    @AfterEach
    void tearDown() {
        enrollmentRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("강좌 수강신청을 수행한다.")
    @Test
    void enrollCourse() {
        // given
        User user1 = userRepository.save(createUser("test1@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course = courseRepository.save(createCourse(user1, category));

        User user2 = userRepository.save(createUser("test2@test.com"));

        // when
        EnrollmentResponseDTO enrollmentResponseDTO =
                enrollmentService.enrollCourse(user2.getId(), course.getId());

        // then
        assertThat(enrollmentResponseDTO)
                .extracting("userId", "courseId")
                .contains(user2.getId(), course.getId());
    }

    @DisplayName("이미 수강한 강좌 수강신청을 수행하면 예외가 발생한다.")
    @Test
    void enrollAlreadyEnrolledCourse() {
        // given
        User user1 = userRepository.save(createUser("test1@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course = courseRepository.save(createCourse(user1, category));

        User user2 = userRepository.save(createUser("test2@test.com"));
        Enrollment enrollment = createEnrollment(user2, course);
        enrollmentRepository.save(enrollment);

        // when - then
        assertThatThrownBy(() -> enrollmentService.enrollCourse(user2.getId(), course.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 등록된 강좌입니다. 회원 ID: " + user2.getId() + ", 강좌 ID: " + course.getId());
    }

    private User createUser(String email) {
        return User.builder()
                .role(Role.STUDENT)
                .name("테스트유저")
                .email(email)
                .hashedPassword("hashedPassword123!")
                .build();
    }

    private Course createCourse(User instructor, Category category) {
        return Course.builder()
                .title("테스트 강좌")
                .level(CourseLevel.BEGINNER)
                .instructor(instructor)
                .category(category)
                .build();
    }

    private Category createCategory() {
        return Category.builder().name("프로그래밍").build();
    }

    private Enrollment createEnrollment(User user, Course course) {
        return Enrollment.builder().user(user).course(course).build();
    }
}
