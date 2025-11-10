package com.example.projectlxp.enrollment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        User instructor = userRepository.save(createUser("instructor@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course = courseRepository.save(createCourse(instructor, category));

        User student = userRepository.save(createUser("student@test.com"));
        Enrollment enrollment = createEnrollment(student, course);
        enrollmentRepository.save(enrollment);

        // when
        boolean result = enrollmentRepository.existsByUserAndCourse(student, course);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("본인이 수강중인 강좌 목록을 확인한다.")
    @Test
    void shouldFindEnrolledCoursesSuccessfully() {
        // given
        User instructor = userRepository.save(createUser("instructor@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course1 = createCourse(instructor, category, "Java Basics");
        Course course2 = createCourse(instructor, category, "Spring Boot");
        Course course3 = createCourse(instructor, category, "Database Fundamentals");
        courseRepository.saveAll(List.of(course1, course2, course3));

        User student = userRepository.save(createUser("student@test.com"));
        Enrollment enrollment = createEnrollment(student, course1);
        Enrollment enrollment2 = createEnrollment(student, course2);
        Enrollment enrollment3 = createEnrollment(student, course3);
        enrollmentRepository.saveAll(List.of(enrollment, enrollment2, enrollment3));

        // when
        List<Enrollment> enrollments =
                enrollmentRepository
                        .findByUserIdWithCourse(student.getId(), Pageable.unpaged())
                        .getContent();

        // then
        assertThat(enrollments)
                .hasSize(3)
                .extracting("progress")
                .containsExactlyInAnyOrder(0, 0, 0);
    }

    @DisplayName("유저가 수강중인 강좌가 없을 때 빈 페이지를 반환한다.")
    @Test
    void shouldReturnEmptyPage_WhenUserHasNoEnrollments() {
        // given
        User student = userRepository.save(createUser("student@test.com"));

        // when
        Page<Enrollment> enrollmentPage =
                enrollmentRepository.findByUserIdWithCourse(student.getId(), Pageable.unpaged());

        // then
        assertThat(enrollmentPage).isNotNull();
        assertThat(enrollmentPage.getTotalElements()).isEqualTo(0);
        assertThat(enrollmentPage.getContent()).isEmpty(); // getContent().hasSize(0)과 동일
        assertThat(enrollmentPage.isEmpty()).isTrue();
    }

    @DisplayName("수강 강좌 목록의 두 번째 페이지를 정상 조회한다.")
    @Test
    void shouldFindSecondPageOfEnrollments() {
        // given
        User instructor = userRepository.save(createUser("instructor@test.com"));
        Category category = categoryRepository.save(createCategory());
        User student = userRepository.save(createUser("student@test.com"));

        List<Course> courses = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            courses.add(createCourse(instructor, category, "Course " + i));
        }
        courseRepository.saveAll(courses);

        List<Enrollment> enrollments = new ArrayList<>();
        for (Course course : courses) {
            enrollments.add(createEnrollment(student, course));
        }
        enrollmentRepository.saveAll(enrollments);

        // when
        Pageable pageable = PageRequest.of(1, 3);
        Page<Enrollment> page =
                enrollmentRepository.findByUserIdWithCourse(student.getId(), pageable);

        // then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(1);
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

    private Course createCourse(User instructor, Category category, String title) {
        return Course.builder()
                .title(title)
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
