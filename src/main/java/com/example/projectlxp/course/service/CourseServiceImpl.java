package com.example.projectlxp.course.service;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.CourseSaveRequest;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.course.service.validator.CourseValidator;
import com.example.projectlxp.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EntityManager entityManager;
    private final CourseValidator validator;

    @Override
    @Transactional
    public CourseDTO saveCourse(CourseSaveRequest request, Long userId) {
        validator.validateCourseCreation(request.categoryId(), userId);
        Course entity =
                request.to(
                        entityManager.getReference(User.class, userId),
                        entityManager.getReference(Category.class, request.categoryId()));
        Course save = courseRepository.save(entity);

        Course course =
                courseRepository
                        .findByIdAndCategoryIdAndInstructorId(
                                save.getId(), request.categoryId(), userId)
                        .orElseThrow(() -> new IllegalArgumentException("강좌 저장에 실패했습니다."));
        return CourseDTO.from(course);
    }
}
