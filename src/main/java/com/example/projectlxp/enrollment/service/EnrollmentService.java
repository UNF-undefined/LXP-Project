package com.example.projectlxp.enrollment.service;

import com.example.projectlxp.enrollment.dto.EnrollmentResponseDTO;

public interface EnrollmentService {
    EnrollmentResponseDTO enrollCourse(Long userId, Long courseId);
}
