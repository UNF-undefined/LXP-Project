package com.example.projectlxp.course.service;

import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.CourseSaveRequest;

public interface CourseService {

    CourseDTO saveCourse(CourseSaveRequest courseDTO, Long userId);
}
