package com.example.projectlxp.course.dto;

import com.example.projectlxp.course.entity.Course;

public record CourseDTO(
        Long id,
        String title,
        String summary,
        String description,
        UserDTO instructor,
        String level,
        int price,
        String thumbnail) {

    public record UserDTO(Long id, String name, String email) {}

    public static CourseDTO from(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getTitle(),
                course.getSummary(),
                course.getDescription(),
                new UserDTO(
                        course.getInstructor().getId(),
                        course.getInstructor().getName(),
                        course.getInstructor().getEmail()),
                course.getLevel().name(),
                course.getPrice(),
                course.getThumbnail());
    }
}
