package com.example.projectlxp.enrollment.service;

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
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
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

        Enrollment enrollment = Enrollment.builder().user(user).course(course).build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return CreateEnrollmentResponseDTO.from(savedEnrollment);
    }

    @Override
    public PagedEnrolledCourseDTO getMyEnrolledCourses(Long userId, Pageable pageable) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new CustomBusinessException("존재하지 않는 회원입니다. ID: " + userId));

        Page<Enrollment> enrollmentPage =
                enrollmentRepository.findByUserIdWithCourse(user.getId(), pageable);
        Page<EnrolledCourseDTO> enrolledCourseDTOPage = enrollmentPage.map(EnrolledCourseDTO::from);
        return PagedEnrolledCourseDTO.from(enrolledCourseDTOPage);
    }
}
