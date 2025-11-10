package com.example.projectlxp.course.entity;

import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    @JsonCreator
    public static CourseLevel from(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException("CourseLevel value cannot be null or blank");
        }

        return Arrays.stream(CourseLevel.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("Invalid CourseLevel value: " + value));
    }
}
