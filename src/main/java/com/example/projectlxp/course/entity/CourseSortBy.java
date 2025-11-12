package com.example.projectlxp.course.entity;

import java.util.Arrays;
import java.util.Objects;

import com.example.projectlxp.course.error.InvalidCourseSortException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseSortBy {
    LATEST("createdAt"),
    POPULARITY("enrollmentCount"),
    RATING("reviewAverage"),
    PRICE_ASC("price"),
    PRICE_DESC("price");

    private final String dbFieldName;

    CourseSortBy(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }

    public String getDbFieldName() {
        return dbFieldName;
    }

    @JsonCreator
    public static CourseLevel from(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new InvalidCourseSortException("Course Level 값이 비어있습니다.");
        }

        return Arrays.stream(CourseLevel.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(InvalidCourseSortException::new);
    }
}
