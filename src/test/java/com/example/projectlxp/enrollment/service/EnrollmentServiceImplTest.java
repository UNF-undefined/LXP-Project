package com.example.projectlxp.enrollment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.IntegrationTestSupport;
import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.category.repository.CategoryRepository;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseLevel;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDetailDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.entity.LectureProgress;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.enrollment.repository.LectureProgressRepository;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.entity.Lecture;
import com.example.projectlxp.lecture.entity.LectureType;
import com.example.projectlxp.lecture.repository.LectureRepository;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.section.repository.SectionRepository;
import com.example.projectlxp.user.entity.Role;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@Transactional
class EnrollmentServiceImplTest extends IntegrationTestSupport {
    @Autowired private EnrollmentService enrollmentService;
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SectionRepository sectionRepository;
    @Autowired private LectureRepository lectureRepository;
    @Autowired private LectureProgressRepository lectureProgressRepository;

    @AfterEach
    void tearDown() {
        lectureProgressRepository.deleteAllInBatch();
        enrollmentRepository.deleteAllInBatch();
        lectureRepository.deleteAllInBatch();
        sectionRepository.deleteAllInBatch();
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

    @DisplayName("수강중인 강좌의 상세 정보를 조회한다.")
    @Test
    void getMyEnrolledCourseDetail_Success() {
        // given
        // 1. 강사, 학생, 카테고리, 강좌 생성
        User instructor = userRepository.save(createUser("instructor@test.com"));
        User student = userRepository.save(createUser("student@test.com"));
        Category category = categoryRepository.save(createCategory());
        Course course = courseRepository.save(createCourse(instructor, category));

        // 2. 섹션 2개 생성 (순서: 1, 2)
        Section section1 = sectionRepository.save(createSection(course, "Section 1", 1));
        Section section2 = sectionRepository.save(createSection(course, "Section 2", 2));

        // 3. 강의 4개 생성
        Lecture lecture1 = lectureRepository.save(createLecture(section1, "Lecture 1-1", 1));
        Lecture lecture2 = lectureRepository.save(createLecture(section1, "Lecture 1-2", 2));
        Lecture lecture3 = lectureRepository.save(createLecture(section2, "Lecture 2-1", 1));
        Lecture lecture4 = lectureRepository.save(createLecture(section2, "Lecture 2-2", 2));

        // 4. 수강 신청 (총 강의 수 4개)
        Enrollment enrollment = enrollmentRepository.save(createEnrollment(student, course, false));

        // 5. 강의 진행도 설정 (4개 중 2개 완료)
        lectureProgressRepository.save(createLectureProgress(enrollment, lecture1, true));
        lectureProgressRepository.save(createLectureProgress(enrollment, lecture2, true));
        lectureProgressRepository.save(createLectureProgress(enrollment, lecture3, false));
        lectureProgressRepository.save(createLectureProgress(enrollment, lecture4, false));

        // when
        EnrolledCourseDetailDTO result =
                enrollmentService.getMyEnrolledCourseDetail(student.getId(), enrollment.getId());

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        EnrolledCourseDetailDTO::getEnrollmentId,
                        EnrolledCourseDetailDTO::getCourseId,
                        EnrolledCourseDetailDTO::getCourseTitle,
                        EnrolledCourseDetailDTO::getInstructorName,
                        EnrolledCourseDetailDTO::getCompletionRate)
                .containsExactly(
                        enrollment.getId(),
                        course.getId(),
                        course.getTitle(),
                        instructor.getName(),
                        50.0);

        assertThat(result.getSections())
                .hasSize(2)
                .satisfiesExactly(
                        sectionDTO ->
                                assertAll(
                                        () ->
                                                assertThat(sectionDTO.getSectionTitle())
                                                        .isEqualTo("Section 1"),
                                        () -> assertThat(sectionDTO.getLectures()).hasSize(2),
                                        () -> {
                                            Assertions.assertNotNull(sectionDTO.getLectures());
                                            assertThat(sectionDTO.getLectures().get(0).getTitle())
                                                    .isEqualTo("Lecture 1-1");
                                        },
                                        () -> {
                                            Assertions.assertNotNull(sectionDTO.getLectures());
                                            assertThat(
                                                            sectionDTO
                                                                    .getLectures()
                                                                    .get(0)
                                                                    .isCompleted())
                                                    .isTrue();
                                        },
                                        () -> {
                                            Assertions.assertNotNull(sectionDTO.getLectures());
                                            assertThat(sectionDTO.getLectures().get(1).getTitle())
                                                    .isEqualTo("Lecture 1-2");
                                        },
                                        () -> {
                                            Assertions.assertNotNull(sectionDTO.getLectures());
                                            assertThat(
                                                            sectionDTO
                                                                    .getLectures()
                                                                    .get(1)
                                                                    .isCompleted())
                                                    .isTrue();
                                        }),
                        sectionDTO ->
                                assertAll(
                                        () ->
                                                assertThat(sectionDTO.getSectionTitle())
                                                        .isEqualTo("Section 2"),
                                        () -> assertThat(sectionDTO.getLectures()).hasSize(2),
                                        () -> {
                                            Assertions.assertNotNull(sectionDTO.getLectures());
                                            assertThat(sectionDTO.getLectures().get(0).getTitle())
                                                    .isEqualTo("Lecture 2-1");
                                        },
                                        () -> {
                                            Assertions.assertNotNull(sectionDTO.getLectures());
                                            assertThat(
                                                            sectionDTO
                                                                    .getLectures()
                                                                    .get(0)
                                                                    .isCompleted())
                                                    .isFalse();
                                        },
                                        () -> {
                                            Assertions.assertNotNull(sectionDTO.getLectures());
                                            assertThat(sectionDTO.getLectures().get(1).getTitle())
                                                    .isEqualTo("Lecture 2-2");
                                        },
                                        () -> {
                                            Assertions.assertNotNull(sectionDTO.getLectures());
                                            assertThat(
                                                            sectionDTO
                                                                    .getLectures()
                                                                    .get(1)
                                                                    .isCompleted())
                                                    .isFalse();
                                        }));
    }

    // 👇 [신규 테스트 케이스] 수강 강좌 상세 조회 (실패 - 권한 없음)
    @DisplayName("다른 사람의 수강 강좌 상세 정보를 조회하면 예외가 발생한다.")
    @Test
    void getMyEnrolledCourseDetail_throwsException_whenNotOwner() {
        // given
        // 1. 강사, 학생1(수강 주인), 학생2(조회 시도자)
        User instructor = userRepository.save(createUser("instructor@test.com"));
        User studentOwner = userRepository.save(createUser("student1@test.com"));
        User studentAttacker = userRepository.save(createUser("student2@test.com"));

        Category category = categoryRepository.save(createCategory());
        Course course = courseRepository.save(createCourse(instructor, category));

        // 2. 수강 신청 (studentOwner가 신청)
        Enrollment enrollment =
                enrollmentRepository.save(createEnrollment(studentOwner, course, false));

        // when & then
        // studentAttacker가 studentOwner의 수강 정보를 조회 시도
        assertThatThrownBy(
                        () ->
                                enrollmentService.getMyEnrolledCourseDetail(
                                        studentAttacker.getId(), enrollment.getId()))
                .isInstanceOf(CustomBusinessException.class)
                .hasMessage("수강신청 정보를 찾을 수 없거나 권한이 없습니다. ID: " + enrollment.getId());
    }

    // 👇 [신규 테스트 케이스] 수강 강좌 상세 조회 (실패 - 회원 없음)
    @DisplayName("존재하지 않는 회원ID로 수강 강좌 상세 조회를 시도하면 예외가 발생한다.")
    @Test
    void getMyEnrolledCourseDetail_throwsException_whenUserNotFound() {
        // given
        Long nonExistentUserId = 9999L;
        Long anyEnrollmentId = 1L;

        // when & then
        assertThatThrownBy(
                        () ->
                                enrollmentService.getMyEnrolledCourseDetail(
                                        nonExistentUserId, anyEnrollmentId))
                .isInstanceOf(CustomBusinessException.class)
                .hasMessage("존재하지 않는 회원입니다. ID: " + nonExistentUserId);
    }

    private Section createSection(Course course, String title, int orderNo) {
        return Section.createSection(course, title, orderNo);
    }

    private Lecture createLecture(Section section, String title, int orderNo) {
        return Lecture.createLecture(
                title, LectureType.VIDEO, orderNo, "http://file.url", section, "10:00");
    }

    private LectureProgress createLectureProgress(
            Enrollment enrollment, Lecture lecture, boolean completed) {
        return LectureProgress.builder()
                .enrollment(enrollment)
                .lecture(lecture)
                .completed(completed)
                .build();
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
        return Enrollment.create(user, course, isHidden);
    }

    private Enrollment createEnrollment(User user, Course course) {
        return createEnrollment(user, course, false);
    }
}
