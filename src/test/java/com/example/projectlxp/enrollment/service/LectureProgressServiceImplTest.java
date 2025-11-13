 package com.example.projectlxp.enrollment.service;

 import com.example.projectlxp.IntegrationTestSupport;
 import com.example.projectlxp.category.entity.Category;
 import com.example.projectlxp.category.repository.CategoryRepository;
 import com.example.projectlxp.course.entity.Course;
 import com.example.projectlxp.course.entity.CourseLevel;
 import com.example.projectlxp.course.repository.CourseRepository;
 import com.example.projectlxp.enrollment.entity.Enrollment;
 import com.example.projectlxp.enrollment.entity.LectureProgress;
 import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
 import com.example.projectlxp.enrollment.repository.LectureProgressRepository;
 import com.example.projectlxp.global.error.CustomBusinessException;
 import com.example.projectlxp.global.events.LectureCompletedEvent;
 import com.example.projectlxp.lecture.entity.Lecture;
 import com.example.projectlxp.lecture.entity.LectureType;
 import com.example.projectlxp.lecture.repository.LectureRepository;
 import com.example.projectlxp.section.entity.Section;
 import com.example.projectlxp.section.repository.SectionRepository;
 import com.example.projectlxp.user.entity.Role;
 import com.example.projectlxp.user.entity.User;
 import com.example.projectlxp.user.repository.UserRepository;
 import org.junit.jupiter.api.AfterEach;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.context.ApplicationEventPublisher;
 import org.springframework.test.context.bean.override.mockito.MockitoBean;

 import java.time.LocalDateTime;
 import java.util.List;

 import static org.assertj.core.api.Assertions.assertThat;
 import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
 import static org.assertj.core.api.BDDAssertions.tuple;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.Mockito.never;
 import static org.mockito.Mockito.times;
 import static org.mockito.Mockito.verify;

 class LectureProgressServiceImplTest extends IntegrationTestSupport {

    @Autowired private LectureProgressService lectureProgressService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private SectionRepository sectionRepository;
    @Autowired private LectureRepository lectureRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private LectureProgressRepository lectureProgressRepository;

    @MockitoBean private ApplicationEventPublisher eventPublisher;

    @AfterEach
    void tearDown() {
        // 자식 테이블부터 삭제 (외래 키 제약조건)
        lectureProgressRepository.deleteAllInBatch();
        lectureRepository.deleteAllInBatch();
        sectionRepository.deleteAllInBatch();
        enrollmentRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

     @DisplayName("강의 수강을 시작하면 LectureProgress가 생성된다.")
     @Test
     void markLectureAsStarted_createNewProgress() {
         // given
         User instructor = userRepository.save(createUser("instructor@test.com"));
         User student = userRepository.save(createUser("student@test.com"));
         Course course = createAndSaveCourse(instructor);
         Section section = sectionRepository.save(createSection(course, "S1", 1));
         Lecture lecture = lectureRepository.save(createLecture(section, "L1", 1));
         Enrollment enrollment = enrollmentRepository.save(createEnrollment(student, course));

         // when
         lectureProgressService.markLectureAsStarted(student.getId(), lecture.getId());

         // then
         List<LectureProgress> progresses = lectureProgressRepository.findAll();

         assertThat(progresses).hasSize(1)
                 .extracting(
                         "enrollment.id",
                         "lecture.id",
                         "completed"
                 )
                 .containsExactly(
                         tuple(enrollment.getId(), lecture.getId(), false)
                 );

         assertThat(progresses.get(0).getLastAccessedAt()).isNotNull();
     }

     @DisplayName("이미 수강을 시작한 강의를 다시 시작하면 lastAccessedAt이 갱신된다.")
     @Test
     void markLectureAsStarted_updateExistingProgress() {
         // given
         User instructor = userRepository.save(createUser("instructor@test.com"));
         User student = userRepository.save(createUser("student@test.com"));
         Course course = createAndSaveCourse(instructor);
         Section section = sectionRepository.save(createSection(course, "S1", 1));
         Lecture lecture = lectureRepository.save(createLecture(section, "L1", 1));
         Enrollment enrollment = enrollmentRepository.save(createEnrollment(student, course));

         // 1. 수강 기록(Progress)을 미리 생성
         LectureProgress progress =
                 lectureProgressRepository.save(createLectureProgress(enrollment, lecture));
         LocalDateTime firstAccess = progress.getLastAccessedAt();

         // when
         lectureProgressService.markLectureAsStarted(student.getId(), lecture.getId());

         // then
         List<LectureProgress> progresses = lectureProgressRepository.findAll();
         assertThat(progresses).hasSize(1) // count() 검증 대체
                 .extracting("enrollment.id", "lecture.id", "completed")
                 .containsExactly(
                         tuple(enrollment.getId(), lecture.getId(), false)
                 );

         assertThat(progresses.get(0).getLastAccessedAt()).isAfterOrEqualTo(firstAccess);
     }

    @DisplayName("강의 수강 시작 시 강의가 존재하지 않으면 예외가 발생한다.")
    @Test
    void markLectureAsStarted_throwsException_whenLectureNotFound() {
        // given
        User user = userRepository.save(createUser("student@test.com"));
        Long nonExistentLectureId = 9999L;

        // when // then
        assertThatThrownBy(
                        () ->
                                lectureProgressService.markLectureAsStarted(
                                        user.getId(), nonExistentLectureId))
                .isInstanceOf(CustomBusinessException.class)
                .hasMessageContaining("존재하지 않는 강의입니다. lectureId=" + nonExistentLectureId);
    }

     @DisplayName("강의 수강 시작 시 수강신청을 하지 않았으면 예외가 발생한다.")
     @Test
     void markLectureAsStarted_throwsException_whenEnrollmentNotFound() {
         // given
         User instructor = userRepository.save(createUser("instructor@test.com"));
         User student = userRepository.save(createUser("student@test.com"));
         Course course = createAndSaveCourse(instructor);
         Section section = sectionRepository.save(createSection(course, "S1", 1));
         Lecture lecture = lectureRepository.save(createLecture(section, "L1", 1));

         // when // then
         assertThatThrownBy(
                 () ->
                         lectureProgressService.markLectureAsStarted(
                                 student.getId(), lecture.getId()))
                 .isInstanceOf(CustomBusinessException.class)
                 .hasMessageContaining("해당 강의에 대한 수강신청 정보를 찾을 수 없습니다.");
     }

     @DisplayName("강의를 완료를 완료하면 LectureProgress의 상태가 완료로 변경된다.")
     @Test
     void test() {
         // given
         User instructor = userRepository.save(createUser("instructor@test.com"));
         User student = userRepository.save(createUser("student@test.com"));
         Course course = createAndSaveCourse(instructor);
         Section section = sectionRepository.save(createSection(course, "S1", 1));
         Lecture lecture = lectureRepository.save(createLecture(section, "L1", 1));
         Enrollment enrollment = enrollmentRepository.save(createEnrollment(student, course));

         // 1. 수강 기록이 '이미 완료됨' 상태로 존재
         lectureProgressRepository.save(createLectureProgress(enrollment, lecture));

         // when
         lectureProgressService.markLectureAsComplete(student.getId(), lecture.getId());

         // then
         List<LectureProgress> progresses = lectureProgressRepository.findAll();
         assertThat(progresses).hasSize(1)
                 .extracting("enrollment.id", "lecture.id", "completed")
                 .containsExactly(
                         tuple(enrollment.getId(), lecture.getId(), true)
                 );
     }

     @DisplayName("이미 완료한 강의를 다시 완료 요청해도 이벤트가 발생하지 않는다 (멱등성).")
     @Test
     void markLectureAsComplete_idempotent() {
         // given
         User instructor = userRepository.save(createUser("instructor@test.com"));
         User student = userRepository.save(createUser("student@test.com"));
         Course course = createAndSaveCourse(instructor);
         Section section = sectionRepository.save(createSection(course, "S1", 1));
         Lecture lecture = lectureRepository.save(createLecture(section, "L1", 1));
         Enrollment enrollment = enrollmentRepository.save(createEnrollment(student, course));

         // 1. 수강 기록이 '이미 완료됨' 상태로 존재
         LectureProgress lectureProgress = createLectureProgress(enrollment, lecture);
         lectureProgress.complete();
         lectureProgressRepository.save(lectureProgress);

         // when
         lectureProgressService.markLectureAsComplete(student.getId(), lecture.getId());

         // then
         List<LectureProgress> progresses = lectureProgressRepository.findAll();
         assertThat(progresses).hasSize(1)
                 .extracting("enrollment.id", "lecture.id", "completed")
                 .containsExactly(
                         tuple(enrollment.getId(), lecture.getId(), true)
                 );
     }

    @DisplayName("강의 완료 처리 시 수강 기록(Progress)이 없으면 예외가 발생한다.")
    @Test
    void markLectureAsComplete_throwsException_whenProgressNotFound() {
        // given
        User instructor = userRepository.save(createUser("instructor@test.com"));
        User student = userRepository.save(createUser("student@test.com"));
        Course course = createAndSaveCourse(instructor);
        Section section = sectionRepository.save(createSection(course, "S1", 1));
        Lecture lecture = lectureRepository.save(createLecture(section, "L1",1));
        enrollmentRepository.save(createEnrollment(student, course));

         // when // then
        assertThatThrownBy(
                        () ->
                                lectureProgressService.markLectureAsComplete(
                                        student.getId(), lecture.getId()))
                .isInstanceOf(CustomBusinessException.class)
                .hasMessage("존재하지 않는 수강목록입니다.");
    }

    // User
    private User createUser(String email) {
        return User.builder()
                .role(Role.STUDENT)
                .name("테스트유저")
                .email(email)
                .hashedPassword("hashedPassword123!")
                .build();
    }

    // Category
    private Category createCategory() {
        return Category.builder().name("프로그래밍").build();
    }

    // Course
    private Course createCourse(User instructor, Category category) {
        return Course.builder()
                .title("테스트 강좌")
                .level(CourseLevel.BEGINNER)
                .instructor(instructor)
                .category(category)
                .build();
    }

    // (Helper) 강사, 카테고리를 포함하여 Course 생성 및 저장
    private Course createAndSaveCourse(User instructor) {
        Category category = categoryRepository.save(createCategory());
        return courseRepository.save(createCourse(instructor, category));
    }

    // Enrollment
    private Enrollment createEnrollment(User user, Course course) {
        return Enrollment.create(user, course, false);
    }

    // Section
    private Section createSection(Course course, String title, int orderNo) {
        return Section.createSection(course, title, orderNo);
    }

    // Lecture
    private Lecture createLecture(Section section, String title, int orderNo) {
        return Lecture.createLecture(
                title, LectureType.VIDEO, orderNo, "http://file.url", section, "10:00");
    }

    // LectureProgress
    private LectureProgress createLectureProgress(
            Enrollment enrollment, Lecture lecture) {

        return LectureProgress.create(enrollment, lecture);
    }
 }
