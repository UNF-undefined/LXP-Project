package com.example.projectlxp.course.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.request.CourseSaveRequest;
import com.example.projectlxp.course.dto.request.CourseSearchRequest;
import com.example.projectlxp.course.dto.request.CourseUpdateRequest;
import com.example.projectlxp.course.dto.response.CourseResponse;

public interface CourseService {

    CourseResponse saveCourse(CourseSaveRequest courseDTO, Long userId);

    CourseResponse searchCourse(Long courseId);

    CourseDTO updateCourse(Long courseId, CourseUpdateRequest request, Long userId);

    Page<CourseDTO> searchCourses(CourseSearchRequest request, Pageable pageable, Long userId);

    Boolean deleteCourse(Long courseId, Long userId);
}
