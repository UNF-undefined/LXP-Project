package com.example.projectlxp.course.service.validator;

import org.springframework.stereotype.Component;

import com.example.projectlxp.category.repository.CategoryRepository;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.user.entity.Role;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@Component
public class CourseValidator {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CourseValidator(
            CourseRepository courseRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public void validateCourseCreation(Long categoryId, Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강사(User) ID입니다."));
        if (user.getRole() != Role.INSTRUCTOR) {
            throw new IllegalArgumentException("강좌를 생성할 권한이 없습니다.");
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("존재하지 않는 카테고리 ID입니다.");
        }
    }
}
