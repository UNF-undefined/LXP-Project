package com.example.projectlxp.enrollment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.category.repository.CategoryRepository;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseLevel;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.global.error.CustomBusinessException;
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
        CreateEnrollmentRequestDTO requestDTO = new CreateEnrollmentRequestDTO(course.getId());

        // when
        CreateEnrollmentResponseDTO createEnrollmentResponseDTO =
                enrollmentService.enrollCourse(user2.getId(), requestDTO);

        // then
        assertThat(createEnrollmentResponseDTO)
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
        CreateEnrollmentRequestDTO requestDTO = new CreateEnrollmentRequestDTO(course.getId());

        // when - then
        assertThatThrownBy(() -> enrollmentService.enrollCourse(user2.getId(), requestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 등록된 강좌입니다. 회원 ID: " + user2.getId() + ", 강좌 ID: " + course.getId());
    }

    @DisplayName("숨김 처리된 강좌를 제외하고 수강중인 강좌를 숨김 처리한다.")
    @Test
    void shouldHideEnrolledCourse() {
        // given
        User user1 = userRepository.save(createUser("test1@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course = courseRepository.save(createCourse(user1, category));

        User user2 = userRepository.save(createUser("test2@test.com"));
        Enrollment enrollment = createEnrollment(user2, course, false);
        enrollmentRepository.save(enrollment);

        // when
        EnrolledCourseDTO enrolledCourseDTO =
                enrollmentService.hideEnrollment(user2.getId(), enrollment.getId());

        // then
        assertThat(enrolledCourseDTO)
                .extracting("enrollmentId", "isHidden")
                .contains(enrollment.getId(), true);
    }

    @DisplayName("숨김 처리된 강좌를 제외하고 수강중인 강좌를 조회한다.")
    @Test
    void shouldShowEnrolledCourse() {
        // given
        User user1 = userRepository.save(createUser("test1@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course1 = courseRepository.save(createCourse("Course 1", user1, category));
        Course course2 = courseRepository.save(createCourse("Course 2", user1, category));
        Course course3 = courseRepository.save(createCourse("Course 3", user1, category));

        User user2 = userRepository.save(createUser("test2@test.com"));
        Enrollment enrollment1 = createEnrollment(user2, course1, false);
        Enrollment enrollment2 = createEnrollment(user2, course2, true);
        Enrollment enrollment3 = createEnrollment(user2, course3, false);

        enrollmentRepository.saveAll(List.of(enrollment1, enrollment2, enrollment3));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        PagedEnrolledCourseDTO result =
                enrollmentService.getMyEnrolledCourses(user2.getId(), false, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getEnrolledCourseDTOList().size()).isEqualTo(2);

        List<Long> enrollmentIds =
                result.getEnrolledCourseDTOList().stream()
                        .map(EnrolledCourseDTO::getEnrollmentId) // DTO에 맞게 수정
                        .toList();

        assertThat(enrollmentIds)
                .containsExactlyInAnyOrder(enrollment1.getId(), enrollment3.getId());
    }

    @DisplayName("숨김 처리된 수강중인 강좌를 다시 보이게 처리한다.")
    @Test
    void shouldUnHideEnrolledCourse() {
        // given
        User user1 = userRepository.save(createUser("test1@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course = courseRepository.save(createCourse(user1, category));

        User user2 = userRepository.save(createUser("test2@test.com"));
        Enrollment enrollment = createEnrollment(user2, course);
        enrollmentRepository.save(enrollment);

        // when
        EnrolledCourseDTO enrolledCourseDTO =
                enrollmentService.unhideEnrollment(user2.getId(), enrollment.getId());

        // then
        assertThat(enrolledCourseDTO)
                .extracting("enrollmentId", "isHidden")
                .contains(enrollment.getId(), false);
    }

    @DisplayName("존재하지 않는 회원ID로 수강 신청을 시도하면 예외가 발생한다.")
    @Test
    void enrollCourse_throwsException_whenUserNotFound() {
        // given
        User courseOwner = userRepository.save(createUser("owner@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course = courseRepository.save(createCourse(courseOwner, category));

        Long nonExistentUserId = 9999L;
        CreateEnrollmentRequestDTO requestDTO = new CreateEnrollmentRequestDTO(course.getId());

        // when // then
        assertThatThrownBy(
                        () -> {
                            enrollmentService.enrollCourse(nonExistentUserId, requestDTO);
                        })
                .isInstanceOf(CustomBusinessException.class)
                .hasMessageContaining("존재하지 않는 회원입니다."); // (서비스의 실제 예외 메시지 확인)
    }

    @DisplayName("존재하지 않는 회원ID로 수강 강좌 조회를 시도하면 예외가 발생한다.")
    @Test
    void getMyEnrolledCourses_throwsException_whenUserNotFound() {
        // given
        Long nonExistentUserId = 9999L; // DB에 절대 존재하지 않을 ID
        Pageable pageable = PageRequest.of(0, 10); // 페이징 파라미터

        // when // then
        assertThatThrownBy(
                        () -> {
                            enrollmentService.getMyEnrolledCourses(
                                    nonExistentUserId, false, pageable);
                        })
                .isInstanceOf(CustomBusinessException.class)
                .hasMessageContaining("존재하지 않는 회원입니다. ID: " + nonExistentUserId);
    }

    @DisplayName("존재하지 않는 강좌ID로 수강 신청을 시도하면 예외가 발생한다.")
    @Test
    void enrollCourse_throwsException_whenCourseNotFound() {
        // given
        User user = userRepository.save(createUser("test1@test.com"));
        Long nonExistentCourseId = 9999L;
        CreateEnrollmentRequestDTO requestDTO = new CreateEnrollmentRequestDTO(nonExistentCourseId);

        // when // then
        assertThatThrownBy(
                        () -> {
                            enrollmentService.enrollCourse(user.getId(), requestDTO);
                        })
                .isInstanceOf(CustomBusinessException.class)
                .hasMessageContaining("존재하지 않는 강좌입니다. ID: " + requestDTO.getCourseId());
    }

    @DisplayName("존재하지 않는 수강ID로 숨김 처리를 시도하면 예외가 발생한다.")
    @Test
    void hideEnrollment_throwsException_whenEnrollmentNotFound() {
        // given
        User user = userRepository.save(createUser("test1@test.com"));
        Long nonExistentEnrollmentId = 9999L; // 존재하지 않는 수강 ID

        // when // then
        assertThatThrownBy(
                        () -> {
                            enrollmentService.hideEnrollment(user.getId(), nonExistentEnrollmentId);
                        })
                .isInstanceOf(CustomBusinessException.class)
                .hasMessageContaining(
                        "존재하지 않는 수강신청입니다. ID: " + nonExistentEnrollmentId); // (실제 예외 메시지에 맞게 수정)
    }

    @DisplayName("존재하지 않는 수강ID로 숨김 해제 처리를 시도하면 예외가 발생한다.")
    @Test
    void unhideEnrollment_throwsException_whenEnrollmentNotFound() {
        // given
        User user = userRepository.save(createUser("test1@test.com"));
        Long nonExistentEnrollmentId = 9999L; // 존재하지 않는 수강 ID

        // when // then
        assertThatThrownBy(
                        () -> {
                            enrollmentService.unhideEnrollment(
                                    user.getId(), nonExistentEnrollmentId);
                        })
                .isInstanceOf(CustomBusinessException.class)
                .hasMessageContaining(
                        "존재하지 않는 수강신청입니다. ID: " + nonExistentEnrollmentId); // (실제 예외 메시지에 맞게 수정)
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

    private Course createCourse(String title, User instructor, Category category) {
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

    private Enrollment createEnrollment(User user, Course course, boolean isHidden) {
        return Enrollment.builder().user(user).course(course).isHidden(isHidden).build();
    }

    private Enrollment createEnrollment(User user, Course course) {
        return createEnrollment(user, course, false);
    }
}
