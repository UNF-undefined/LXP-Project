package com.example.projectlxp.course.dto;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.user.entity.User;

public record CourseDTO(
        Long id,
        String title,
        String summary,
        String description,
        UserDTO instructor,
        String level,
        int price,
        String thumbnail,
        CategoryDTO category) {

    public record UserDTO(Long id, String name, String email) {
        public static UserDTO of(User user) {
            return new UserDTO(user.getId(), user.getName(), user.getEmail());
        }
    }

    public record CategoryDTO(Long id, String name, CategoryDTO parent) {
        public static CategoryDTO of(Category category) {
            return new CategoryDTO(
                    category.getId(),
                    category.getName(),
                    category.getParent() != null ? CategoryDTO.of(category.getParent()) : null);
        }
    }

    public static CourseDTO from(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getTitle(),
                course.getSummary(),
                course.getDescription(),
                UserDTO.of(course.getInstructor()),
                course.getLevel().name(),
                course.getPrice(),
                course.getThumbnail(),
                CategoryDTO.of(course.getCategory()));
    }
}
