package com.example.projectlxp.course.service;

import com.example.projectlxp.course.dto.CourseResponse;
import com.example.projectlxp.course.dto.CourseSaveRequest;

public interface CourseService {

    CourseResponse saveCourse(CourseSaveRequest courseDTO, Long userId);

    CourseResponse searchCourse(Long courseId);
}
