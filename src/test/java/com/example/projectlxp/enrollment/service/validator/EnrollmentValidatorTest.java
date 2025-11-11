package com.example.projectlxp.enrollment.service.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.user.entity.Role;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@ActiveProfiles("test")
@SpringBootTest
class EnrollmentValidatorTest {
    @Autowired private EnrollmentValidator enrollmentValidator;
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

    @DisplayName("수강 목록에 대한 권한은 본인만 있다.")
    @Test
    void validateOwnership_throwsException_whenNotOwner() {
        // given
        User user1 = userRepository.save(createUser("test1@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course = courseRepository.save(createCourse(user1, category));

        User user2 = userRepository.save(createUser("test2@test.com"));
        Enrollment enrollment = enrollmentRepository.save(createEnrollment(user2, course));
        User user3 = userRepository.save(createUser("test3@test.com"));

        // when // then
        assertThatThrownBy(
                        () -> {
                            enrollmentValidator.validateOwnership(user3.getId(), enrollment);
                        })
                .isInstanceOf(CustomBusinessException.class)
                .hasMessage(
                        "수강신청을 숨길 권한이 없습니다. 회원 ID: "
                                + user3.getId()
                                + ", 수강신청 ID: "
                                + enrollment.getId());
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

    private Enrollment createEnrollment(User user, Course course, boolean isHidden) {
        return Enrollment.create(user, course, isHidden);
    }

    private Enrollment createEnrollment(User user, Course course) {
        return createEnrollment(user, course, false);
    }
}
