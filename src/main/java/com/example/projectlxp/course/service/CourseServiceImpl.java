package com.example.projectlxp.course.service;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.CourseResponse;
import com.example.projectlxp.course.dto.CourseSaveRequest;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.error.CourseNotFoundException;
import com.example.projectlxp.course.error.CourseNotSavedException;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.course.service.validator.CourseValidator;
import com.example.projectlxp.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EntityManager entityManager;
    private final CourseValidator validator;

    @Override
    @Transactional
    public CourseResponse saveCourse(CourseSaveRequest request, Long userId) {
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
                        .orElseThrow(CourseNotSavedException::new);
        return new CourseResponse(CourseDTO.from(course), null);
    }

    @Override
    public CourseResponse searchCourse(Long courseId) {
        Course course =
                courseRepository
                        .findByIdWithInstructorAndCategory(courseId)
                        .orElseThrow(CourseNotFoundException::new);

        return new CourseResponse(CourseDTO.from(course), null);
    }
}
