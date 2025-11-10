package com.example.projectlxp.enrollment.service;

import org.springframework.data.domain.Pageable;

import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;

public interface EnrollmentService {
    CreateEnrollmentResponseDTO enrollCourse(Long userId, CreateEnrollmentRequestDTO requestDTO);

    PagedEnrolledCourseDTO getMyEnrolledCourses(Long userId, Pageable pageable);
}
