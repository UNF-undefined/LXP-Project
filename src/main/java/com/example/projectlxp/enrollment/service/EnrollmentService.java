package com.example.projectlxp.enrollment.service;

import org.springframework.data.domain.Pageable;

import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDetailDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;

public interface EnrollmentService {
    CreateEnrollmentResponseDTO enrollCourse(Long userId, CreateEnrollmentRequestDTO requestDTO);

    EnrolledCourseDetailDTO getMyEnrolledCourseDetail(Long userId, Long enrollmentId);

    PagedEnrolledCourseDTO getMyEnrolledCourses(Long userId, Boolean isHidden, Pageable pageable);

    EnrolledCourseDTO hideEnrollment(Long userId, Long enrollmentId);

    EnrolledCourseDTO unhideEnrollment(Long userId, Long enrollmentId);
}
