package com.example.projectlxp.enrollment.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import com.example.projectlxp.enrollment.dto.response.EnrolledSectionDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.entity.LectureProgress;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.enrollment.repository.LectureProgressRepository;
import com.example.projectlxp.enrollment.repository.projection.CompletedLectureCountProjection;
import com.example.projectlxp.enrollment.repository.projection.LectureCountProjection;
import com.example.projectlxp.enrollment.service.validator.EnrollmentValidator;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.entity.Lecture;
import com.example.projectlxp.lecture.repository.LectureRepository;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.section.repository.SectionRepository;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final LectureRepository lectureRepository;
    private final LectureProgressRepository lectureProgressRepository;
    private final EnrollmentValidator enrollmentValidator;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            SectionRepository sectionRepository,
            LectureRepository lectureRepository,
            LectureProgressRepository lectureProgressRepository,
            EnrollmentValidator enrollmentValidator) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
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
        List<Section> enrolledCourseSections =
                sectionRepository.findAllByCourseOrderByOrderNoAsc(enrolledCourse);

        Map<Long, Boolean> progressMap = createLectureProgressMap(enrollment);
        double completionRate = calculateCompletionRate(enrolledCourseLectures.size(), progressMap);

        List<EnrolledSectionDTO> sectionDTOs =
                createEnrolledSectionDTOs(
                        enrolledCourseSections, enrolledCourseLectures, progressMap);
        return EnrolledCourseDetailDTO.of(enrollment, enrolledCourse, completionRate, sectionDTOs);
    }

    /**
     * 섹션 목록과 전체 강의 목록을 조합하여, 계층 구조를 가진 EnrolledSectionDTO 리스트를 생성합니다.
     *
     * @param sections 강좌의 모든 섹션 (순서대로 정렬됨)
     * @param lectures 강좌의 모든 강의
     * @param progressMap 강의별 완료 여부 맵
     * @return List<EnrolledSectionDTO>
     */
    private List<EnrolledSectionDTO> createEnrolledSectionDTOs(
            List<Section> sections, List<Lecture> lectures, Map<Long, Boolean> progressMap) {

        // Lecture DTO를 만들고, Section ID를 기준으로 그룹화
        Map<Long, List<EnrolledLectureDTO>> lectureDTOsBySectionId =
                lectures.stream()
                        .collect(
                                Collectors.groupingBy(
                                        lecture -> lecture.getSection().getId(),
                                        Collectors.mapping(
                                                lecture ->
                                                        EnrolledLectureDTO.of(lecture, progressMap),
                                                Collectors.toList())));

        // 정렬된 섹션 목록을 순회하며 DTO 조립
        return sections.stream()
                .map(
                        section -> {
                            // 1번 맵에서 현재 섹션에 해당하는 강의 DTO 리스트를 가져옴
                            List<EnrolledLectureDTO> lecturesForThisSection =
                                    lectureDTOsBySectionId.getOrDefault(
                                            section.getId(), Collections.emptyList());

                            return EnrolledSectionDTO.of(section, lecturesForThisSection);
                        })
                .toList();
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

        Page<EnrolledCourseDTO> enrolledCourseDTOPage = createEnrolledCourseDTOPage(enrollmentPage);
        return PagedEnrolledCourseDTO.from(enrolledCourseDTOPage);
    }

    /**
     * Enrollment 페이지를 받아, N+1 문제 없이 진도율을 계산하여 EnrolledCourseDTO 페이지로 변환합니다.
     *
     * @param enrollmentPage 원본 Enrollment 페이지
     * @return 진도율이 계산된 DTO 페이지
     */
    private Page<EnrolledCourseDTO> createEnrolledCourseDTOPage(Page<Enrollment> enrollmentPage) {

        List<Enrollment> enrollments = enrollmentPage.getContent();
        if (enrollments.isEmpty()) {
            return Page.empty();
        }

        Map<Long, Long> totalLectureMap = getTotalLectureMap(enrollments);
        Map<Long, Long> completedLectureMap = getCompletedLectureMap(enrollments);

        List<EnrolledCourseDTO> dtoList =
                createEnrolledCourseDTOList(enrollments, totalLectureMap, completedLectureMap);

        return new PageImpl<>(
                dtoList, enrollmentPage.getPageable(), enrollmentPage.getTotalElements());
    }

    /** Enrollment 리스트를 받아 강좌별 총 강의 수를 Map으로 반환합니다. (Batch 조회) */
    private Map<Long, Long> getTotalLectureMap(List<Enrollment> enrollments) {
        Set<Long> courseIds =
                enrollments.stream().map(e -> e.getCourse().getId()).collect(Collectors.toSet());
        List<LectureCountProjection> totalLectureCountsByCourseIds =
                lectureRepository.findLectureCountsByCourseIds(courseIds);
        return totalLectureCountsByCourseIds.stream()
                .collect(
                        Collectors.toMap(
                                LectureCountProjection::getCourseId,
                                LectureCountProjection::getLectureCount));
    }

    /** Enrollment 리스트를 받아 수강별 완료 강의 수를 Map으로 반환합니다. (Batch 조회) */
    private Map<Long, Long> getCompletedLectureMap(List<Enrollment> enrollments) {
        Set<Long> enrollmentIds =
                enrollments.stream().map(Enrollment::getId).collect(Collectors.toSet());
        List<CompletedLectureCountProjection> completedLectureCountsByEnrollmentIds =
                lectureProgressRepository.findCompletedLectureCountsByEnrollmentIds(enrollmentIds);
        return completedLectureCountsByEnrollmentIds.stream()
                .collect(
                        Collectors.toMap(
                                CompletedLectureCountProjection::getEnrollmentId,
                                CompletedLectureCountProjection::getCompletedCount));
    }

    /** Enrollment 리스트와 진도율 데이터를 조합하여 DTO 리스트를 생성합니다. */
    private List<EnrolledCourseDTO> createEnrolledCourseDTOList(
            List<Enrollment> enrollments,
            Map<Long, Long> totalLectureMap,
            Map<Long, Long> completedLectureMap) {

        return enrollments.stream()
                .map(
                        enrollment -> {
                            long courseId = enrollment.getCourse().getId();
                            long enrollmentId = enrollment.getId();

                            long totalLectures = totalLectureMap.getOrDefault(courseId, 0L);
                            long completedLectures =
                                    completedLectureMap.getOrDefault(enrollmentId, 0L);

                            double completionRate =
                                    calculateCompletionRate(totalLectures, completedLectures);

                            return EnrolledCourseDTO.of(enrollment, completionRate);
                        })
                .toList();
    }

    /** 진도율(%)을 계산하는 유틸리티 메서드 */
    private double calculateCompletionRate(long totalLectures, long completedLectures) {
        if (totalLectures == 0) {
            return 0.0;
        }
        return ((double) completedLectures / totalLectures) * 100.0;
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
