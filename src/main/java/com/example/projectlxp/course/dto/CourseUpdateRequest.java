package com.example.projectlxp.course.dto;

import com.example.projectlxp.course.entity.CourseLevel;

public record CourseUpdateRequest(
        String title,
        String summary,
        String description,
        CourseLevel level,
        Integer price,
        String thumbnailUrl,
        Long categoryId) {}
