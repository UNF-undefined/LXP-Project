package com.example.projectlxp.course.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.category.repository.CategoryRepository;
import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.request.CourseSaveRequest;
import com.example.projectlxp.course.dto.request.CourseUpdateRequest;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseLevel;
import com.example.projectlxp.course.error.CourseNotFoundException;
import com.example.projectlxp.course.repository.CourseRepository;
import com.example.projectlxp.course.service.validator.CourseValidator;
import com.example.projectlxp.section.repository.SectionRepository;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @InjectMocks private CourseServiceImpl courseService;

    @Mock private CourseRepository courseRepository;
    @Mock private CourseValidator courseValidator;
    @Mock private UserRepository userRepository;
    @Mock private SectionRepository sectionRepository;
    @Mock private CategoryRepository categoryRepository;

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
        when(userRepository.getReferenceById(any())).thenReturn(instructor);
        when(categoryRepository.getReferenceById(any())).thenReturn(category);
        when(courseRepository.findByIdWithInstructorAndCategory(any()))
                .thenReturn(Optional.of(course));
        when(sectionRepository.findAllByCourseIdOrderByOrderNoAsc(any())).thenReturn(List.of());
        CourseDTO courseDTO = courseService.saveCourse(request, 1L).course();

        // then
        assertAll(
                () -> assertEquals("test", courseDTO.title()),
                () -> assertEquals("test summary", courseDTO.summary()),
                () -> assertEquals("test description", courseDTO.description()),
                () -> assertEquals(1000, courseDTO.price()),
                () -> assertNull(courseDTO.thumbnail()));
    }

    @Test
    void 단일_강좌를_조회한다() {
        // given
        User instructor = User.builder().name("testName").email("test@test.com").build();
        Category category = Category.builder().name("testName").build();

        Course course =
                Course.builder()
                        .title("testTitle")
                        .description("testDescription")
                        .level(CourseLevel.BEGINNER)
                        .category(category)
                        .instructor(instructor)
                        .build();

        // when
        when(courseRepository.findByIdWithInstructorAndCategory(1L))
                .thenReturn(Optional.of(course));
        when(sectionRepository.findAllByCourseIdOrderByOrderNoAsc(any())).thenReturn(List.of());
        CourseDTO dto = courseService.searchCourse(1L).course();

        // then
        assertAll(
                () -> assertEquals("testTitle", dto.title()),
                () -> assertEquals("testDescription", dto.description()));
    }

    @Test
    void 강좌를_수정한다() {
        User instructor = User.builder().name("testName").email("test@test.com").build();
        Category category = Category.builder().name("testName").build();

        Course course =
                Course.builder()
                        .title("testTitle")
                        .description("testDescription")
                        .level(CourseLevel.BEGINNER)
                        .category(category)
                        .instructor(instructor)
                        .build();
        CourseUpdateRequest request =
                new CourseUpdateRequest(
                        "updatedTitle",
                        "updatedSummary",
                        "updatedDescription",
                        CourseLevel.INTERMEDIATE,
                        2000,
                        null,
                        2L);

        when(courseRepository.findByIdWithInstructorAndCategory(1L))
                .thenReturn(Optional.of(course));
        when(categoryRepository.findByIdOptimize(2L)).thenReturn(Optional.of(category));
        doNothing().when(courseValidator).validateCourseUpdate(any(), any(), any());
        CourseDTO dto = courseService.updateCourse(1L, request, 1L);

        assertAll(
                () -> assertEquals("updatedTitle", dto.title()),
                () -> assertEquals("updatedSummary", dto.summary()),
                () -> assertEquals("updatedDescription", dto.description()),
                () -> assertEquals(CourseLevel.INTERMEDIATE.name(), dto.level()),
                () -> assertEquals(2000, dto.price()));
    }

    @Test
    void 강좌를_삭제한다() {
        User instructor = User.builder().name("testName").email("test@test.com").build();
        Category category = Category.builder().name("testName").build();

        Course course =
                Course.builder()
                        .title("testTitle")
                        .description("testDescription")
                        .level(CourseLevel.BEGINNER)
                        .category(category)
                        .instructor(instructor)
                        .build();

        when(courseRepository.findByIdAndInstructorId(anyLong(), anyLong()))
                .thenReturn(Optional.of(course));
        Boolean result = courseService.deleteCourse(1L, 1L);

        assertAll(
                () -> assertEquals(true, result),
                () -> verify(courseRepository, times(1)).delete(course));
    }

    @Test
    void 강좌가_없으면_예외를_발생시킨다() {
        when(courseRepository.findByIdAndInstructorId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteCourse(1L, 1L))
                .isInstanceOf(CourseNotFoundException.class);
    }
}
