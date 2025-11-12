package com.example.projectlxp.course.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.projectlxp.course.dto.request.CourseSearchRequest;
import com.example.projectlxp.course.entity.Course;

public interface CourseQueryRepository {

    Page<Course> searchAll(CourseSearchRequest request, Pageable pageable);
}
