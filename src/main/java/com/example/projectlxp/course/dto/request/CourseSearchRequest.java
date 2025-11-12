package com.example.projectlxp.course.dto.request;

import java.util.List;

import com.example.projectlxp.course.entity.CourseSortBy;

public record CourseSearchRequest(
        List<Long> categoryIds, List<Long> instructorIds, CourseSortBy sortBy) {}
