package com.example.projectlxp.course.entity;

import java.util.Arrays;
import java.util.Objects;

import com.example.projectlxp.course.error.InvalidCourseLevelException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    @JsonCreator
    public static CourseLevel from(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new InvalidCourseLevelException("Course Level 값이 비어있습니다.");
        }

        return Arrays.stream(CourseLevel.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(InvalidCourseLevelException::new);
    }
}
