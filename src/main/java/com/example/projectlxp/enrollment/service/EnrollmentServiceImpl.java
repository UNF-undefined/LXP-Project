package com.example.projectlxp.enrollment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.enrollment.dto.EnrollmentResponseDTO;
import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.enrollment.repository.EnrollmentRepository;
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
    public EnrollmentResponseDTO enrollCourse(Long userId, Long courseId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + userId));

        Course course =
                courseRepository
                        .findById(courseId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "존재하지 않는 강좌입니다. ID: " + courseId));

        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new IllegalStateException(
                    "이미 등록된 강좌입니다. 회원 ID: " + userId + ", 강좌 ID: " + courseId);
        }

        Enrollment enrollment = Enrollment.builder().user(user).course(course).build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return EnrollmentResponseDTO.from(savedEnrollment);
    }
}
