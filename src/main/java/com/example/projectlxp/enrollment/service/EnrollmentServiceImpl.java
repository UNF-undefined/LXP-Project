package com.example.projectlxp.enrollment.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDetailDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledLectureDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.entity.LectureProgress;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.enrollment.repository.LectureProgressRepository;
import com.example.projectlxp.enrollment.service.validator.EnrollmentValidator;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.entity.Lecture;
import com.example.projectlxp.lecture.repository.LectureRepository;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final EnrollmentValidator enrollmentValidator;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            LectureRepository lectureRepository,
            LectureProgressRepository lectureProgressRepository,
            EnrollmentValidator enrollmentValidator) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.lectureProgressRepository = lectureProgressRepository;
        this.enrollmentValidator = enrollmentValidator;
    }

    @Override
    @Transactional
    public CreateEnrollmentResponseDTO enrollCourse(
            Long userId, CreateEnrollmentRequestDTO requestDTO) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "존재하지 않는 회원입니다. ID: " + userId,
                                                HttpStatus.NOT_FOUND));

        Course course =
                courseRepository
                        .findById(requestDTO.getCourseId())
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "존재하지 않는 강좌입니다. ID: " + requestDTO.getCourseId(),
                                                HttpStatus.NOT_FOUND));

        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new CustomBusinessException(
                    "이미 등록된 강좌입니다. 회원 ID: " + userId + ", 강좌 ID: " + requestDTO.getCourseId(),
                    HttpStatus.CONFLICT);
        }

        Enrollment enrollment = Enrollment.create(user, course, false);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return CreateEnrollmentResponseDTO.from(savedEnrollment);
    }

    @Override
    public EnrolledCourseDetailDTO getMyEnrolledCourseDetail(Long userId, Long enrollmentId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new CustomBusinessException("존재하지 않는 회원입니다. ID: " + userId));

        Enrollment enrollment =
                enrollmentRepository
                        .findDetailByIdAndUserId(enrollmentId, user.getId())
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "수강신청 정보를 찾을 수 없거나 권한이 없습니다. ID: " + enrollmentId));

        Course enrolledCourse = enrollment.getCourse();
        List<Lecture> enrolledCourseLectures =
                lectureRepository.findLecturesByCourse(enrolledCourse);

        Map<Long, Boolean> progressMap = createLectureProgressMap(enrollment);
        double completionRate = calculateCompletionRate(enrolledCourseLectures.size(), progressMap);
        List<EnrolledLectureDTO> lectureDTOs =
                createEnrolledLectureDTOs(enrolledCourseLectures, progressMap);

        return EnrolledCourseDetailDTO.of(enrollment, enrolledCourse, completionRate, lectureDTOs);
    }

    /**
     * LectureProgress 리스트를 (Key: Lecture ID, Value: 완료 여부) Map으로 변환하는 메서드.
     *
     * @param enrollment 수강 중인 Enrollment 엔티티
     * @return Map<Long, Boolean> (LectureId, Completed)
     */
    private Map<Long, Boolean> createLectureProgressMap(Enrollment enrollment) {
        List<LectureProgress> progresses =
                lectureProgressRepository.findAllByEnrollmentWithLecture(enrollment);
        return progresses.stream()
                .collect(
                        Collectors.toMap(
                                progress -> progress.getLecture().getId(),
                                LectureProgress::isCompleted));
    }

    /**
     * 전체 진도율(%)을 계산하는 메서드
     *
     * @param totalLectures 강좌의 전체 강의 수
     * @param progressMap Key: Lecture ID, Value: 완료 여부(boolean)
     * @return 진도율 (0.0 ~ 100.0)
     */
    private double calculateCompletionRate(long totalLectures, Map<Long, Boolean> progressMap) {
        long completedLectures =
                progressMap.values().stream().filter(Boolean::booleanValue).count();

        if (totalLectures == 0) {
            return 0.0;
        }

        return ((double) completedLectures / totalLectures) * 100.0;
    }

    private List<EnrolledLectureDTO> createEnrolledLectureDTOs(
            List<Lecture> enrolledCourseLectures, Map<Long, Boolean> progressMap) {
        return enrolledCourseLectures.stream()
                .map(lecture -> EnrolledLectureDTO.of(lecture, progressMap))
                .toList();
    }

    @Override
    public PagedEnrolledCourseDTO getMyEnrolledCourses(
            Long userId, Boolean isHidden, Pageable pageable) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new CustomBusinessException("존재하지 않는 회원입니다. ID: " + userId));

        Page<Enrollment> enrollmentPage =
                enrollmentRepository.findVisibleByUserIdWithCourse(
                        user.getId(), isHidden, pageable);

        Page<EnrolledCourseDTO> enrolledCourseDTOPage = enrollmentPage.map(EnrolledCourseDTO::from);
        return PagedEnrolledCourseDTO.from(enrolledCourseDTOPage);
    }

    @Override
    @Transactional
    public EnrolledCourseDTO hideEnrollment(Long userId, Long enrollmentId) {
        Enrollment enrollment =
                enrollmentRepository
                        .findById(enrollmentId)
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "존재하지 않는 수강신청입니다. ID: " + enrollmentId,
                                                HttpStatus.NOT_FOUND));

        enrollmentValidator.validateOwnership(userId, enrollment);
        enrollment.hide();

        return EnrolledCourseDTO.from(enrollment);
    }

    @Override
    @Transactional
    public EnrolledCourseDTO unhideEnrollment(Long userId, Long enrollmentId) {
        Enrollment enrollment =
                enrollmentRepository
                        .findById(enrollmentId)
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "존재하지 않는 수강신청입니다. ID: " + enrollmentId,
                                                HttpStatus.NOT_FOUND));

        enrollmentValidator.validateOwnership(userId, enrollment);
        enrollment.unhide();

        return EnrolledCourseDTO.from(enrollment);
    }
}
