package com.example.projectlxp.enrollment.repository;

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
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.user.entity.Role;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@ActiveProfiles("test")
@SpringBootTest
class EnrollmentRepositoryTest {
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

    @DisplayName("학생과 강좌로 이미 등록된 내역이 있으면 true를 반환한다.")
    @Test
    void test() {
        // given
        User instructor = userRepository.save(createUser("name1", "instructor@test.com"));
        User student = userRepository.save(createUser("name2", "student@test.com"));
        Category category = categoryRepository.save(createCategory());

        Course course = courseRepository.save(createCourse(instructor, category));
        Enrollment enrollment = createEnrollment(student, course);
        enrollmentRepository.save(enrollment);

        // when
        boolean result = enrollmentRepository.existsByUserAndCourse(student, course);

        // then
        assertThat(result).isTrue();
    }

    private User createUser(String nickname, String email) {
        return User.builder()
                .role(Role.STUDENT)
                .name("테스트유저")
                .nickname(nickname)
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
