package com.example.projectlxp.course.service;

import static java.util.Objects.nonNull;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.category.error.CategoryNotFoundException;
import com.example.projectlxp.category.repository.CategoryRepository;
import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.request.CourseSaveRequest;
import com.example.projectlxp.course.dto.request.CourseSearchRequest;
import com.example.projectlxp.course.dto.request.CourseUpdateRequest;
import com.example.projectlxp.course.dto.response.CourseResponse;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.error.CourseNotFoundException;
import com.example.projectlxp.course.error.CourseNotSavedException;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.course.service.validator.CourseValidator;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.section.repository.SectionRepository;
import com.example.projectlxp.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final CourseValidator validator;

    @Override
    @Transactional
    public CourseResponse saveCourse(CourseSaveRequest request, Long userId) {
        validator.validateCourseCreation(request.categoryId(), userId);
        Course entity =
                request.to(
                        userRepository.getReferenceById(userId),
                        categoryRepository.getReferenceById(request.categoryId()));
        Course save = courseRepository.save(entity);

        Course course =
                courseRepository
                        .findByIdWithInstructorAndCategory(save.getId())
                        .orElseThrow(CourseNotSavedException::new);

        List<Section> sections =
                sectionRepository.findAllByCourseIdOrderByOrderNoAsc(course.getId());
        return CourseResponse.of(course, sections);
    }

    @Override
    public CourseResponse searchCourse(Long courseId) {
        Course course =
                courseRepository
                        .findByIdWithInstructorAndCategory(courseId)
                        .orElseThrow(CourseNotFoundException::new);

        List<Section> sections =
                sectionRepository.findAllByCourseIdOrderByOrderNoAsc(course.getId());
        return CourseResponse.of(course, sections);
    }

    @Override
    @Transactional
    public CourseDTO updateCourse(Long courseId, CourseUpdateRequest request, Long userId) {
        Course course =
                courseRepository
                        .findByIdWithInstructorAndCategory(courseId)
                        .orElseThrow(CourseNotFoundException::new);

        validator.validateCourseUpdate(course, userId, request.categoryId());
        Category category = null;
        if (nonNull(request.categoryId())) {
            category =
                    categoryRepository
                            .findByIdOptimize(request.categoryId())
                            .orElseThrow(CategoryNotFoundException::new);
        }
        course.updateDetails(
                request.title(),
                request.summary(),
                request.description(),
                request.price(),
                request.thumbnailUrl(),
                request.level(),
                category);
        return CourseDTO.from(course);
    }

    @Override
    public Page<CourseDTO> searchCourses(CourseSearchRequest request, Pageable pageable) {
        return courseRepository.searchAll(request, pageable).map(CourseDTO::from);
    }

    @Override
    @Transactional
    public Boolean deleteCourse(Long courseId, Long userId) {
        Course course =
                courseRepository
                        .findByIdAndInstructorId(courseId, userId)
                        .orElseThrow(CourseNotFoundException::new);
        courseRepository.delete(course);
        return true;
    }
}
