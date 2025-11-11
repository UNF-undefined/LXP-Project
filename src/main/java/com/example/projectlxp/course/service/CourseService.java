package com.example.projectlxp.course.service;

import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.CourseResponse;
import com.example.projectlxp.course.dto.CourseSaveRequest;
import com.example.projectlxp.course.dto.CourseUpdateRequest;

public interface CourseService {

    CourseResponse saveCourse(CourseSaveRequest courseDTO, Long userId);

    CourseResponse searchCourse(Long courseId);

    CourseDTO updateCourse(Long courseId, CourseUpdateRequest request, Long userId);
}
