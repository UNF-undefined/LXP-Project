package com.example.projectlxp.course.service.validator;

import static java.util.Objects.nonNull;

import org.springframework.stereotype.Component;

import com.example.projectlxp.category.error.CategoryNotFoundException;
import com.example.projectlxp.category.repository.CategoryRepository;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.error.CourseUpdateDeniedException;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.user.repository.UserRepository;

@Component
public class CourseValidator {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CourseValidator(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public void validateCourseCreation(Long categoryId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomBusinessException("존재하지 않는 강사(User) ID입니다.");
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException();
        }
    }

    public void validateCourseUpdate(Course course, Long userId, Long categoryId) {
        if (nonNull(userId) && !course.getInstructor().getId().equals(userId)) {
            throw new CourseUpdateDeniedException();
        }

        if (nonNull(categoryId) && !categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException();
        }
    }
}
