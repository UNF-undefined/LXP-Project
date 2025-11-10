package com.example.projectlxp.course.dto;

public record CourseResponse(CourseDTO course, String section) {

    public static CourseResponse of(CourseDTO course, String section) {
        // TODO section, lesson 등 필요한 데이터를 추가하여 반환 예정
        return new CourseResponse(course, section);
    }
}
