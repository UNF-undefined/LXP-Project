package com.example.projectlxp.course.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.CourseSaveRequest;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseLevel;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.course.service.validator.CourseValidator;
import com.example.projectlxp.user.entity.User;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @InjectMocks private CourseServiceImpl courseService;

    @Mock private CourseRepository courseRepository;
    @Mock private CourseValidator courseValidator;
    @Mock private EntityManager entityManager;

    @Test
    void 강좌를_생성한다() {
        // given
        CourseSaveRequest request =
                new CourseSaveRequest(
                        "test",
                        "test summary",
                        "test description",
                        CourseLevel.BEGINNER,
                        1000,
                        null,
                        1L);
        User instructor = User.builder().name("testName").email("test@test.com").build();
        Category category = Category.builder().name("testName").build();
        Course course = request.to(instructor, category);

        // when
        doNothing().when(courseValidator).validateCourseCreation(any(), any());
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(entityManager.getReference(eq(User.class), any())).thenReturn(instructor);
        when(entityManager.getReference(eq(Category.class), any())).thenReturn(category);
        when(courseRepository.findByIdAndCategoryIdAndInstructorId(any(), any(), any()))
                .thenReturn(Optional.of(course));
        CourseDTO courseDTO = courseService.saveCourse(request, 1L);

        // then
        assertAll(
                () -> assertEquals("test", courseDTO.title()),
                () -> assertEquals("test summary", courseDTO.summary()),
                () -> assertEquals("test description", courseDTO.description()),
                () -> assertEquals(1000, courseDTO.price()),
                () -> assertNull(courseDTO.thumbnail()));
    }
}
