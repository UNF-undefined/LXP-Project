package com.example.projectlxp.course.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseLevel;
import com.example.projectlxp.user.entity.User;

public record CourseSaveRequest(
        @NotBlank String title,
        @NotBlank String summary,
        @NotBlank String description,
        @NotNull CourseLevel level,
        @NotNull @Min(value = 0) Integer price,
        String thumbnailUrl,
        @NotNull Long categoryId) {

    public Course to(User instructor, Category category) {
        return Course.builder()
                .title(title)
                .summary(summary)
                .description(description)
                .level(level)
                .price(price)
                .thumbnail(thumbnailUrl)
                .category(category)
                .instructor(instructor)
                .build();
    }
}
