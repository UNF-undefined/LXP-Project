package com.example.projectlxp.course.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.request.CourseSaveRequest;
import com.example.projectlxp.course.dto.request.CourseSearchRequest;
import com.example.projectlxp.course.dto.request.CourseUpdateRequest;
import com.example.projectlxp.course.dto.response.CourseResponse;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseLevel;
import com.example.projectlxp.course.entity.CourseSortBy;
import com.example.projectlxp.course.service.CourseService;
import com.example.projectlxp.global.jwt.JwtTokenProvider;
import com.example.projectlxp.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@WithMockUser(username = "1", roles = "STUDENT")
@ActiveProfiles("test")
@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {
    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CourseService courseService;

    @MockitoBean private JwtTokenProvider jwtTokenProvider;

    private final Long MOCK_USER_ID = 1L;
    private final Long MOCK_COURSE_ID = 10L;

    private CourseDTO createMockCourseDTO() {
        Course build =
                Course.builder()
                        .title("Mock Course Title")
                        .price(10000)
                        .instructor(User.builder().name("123").email("test@naver.com").build())
                        .level(CourseLevel.BEGINNER)
                        .category(new Category("Mock Category", false, null, null))
                        .build();
        return CourseDTO.from(build);
    }

    private CourseResponse createMockCourseResponse() {
        return new CourseResponse(
                new CourseDTO(
                        MOCK_COURSE_ID,
                        "Mock Detailed Course",
                        "Mock Summary",
                        "Mock Description",
                        null,
                        CourseLevel.BEGINNER.name(),
                        15000,
                        null,
                        null),
                null);
    }

    @Test
    void 강좌_목록_검색_및_페이징_테스트() throws Exception {
        // Given
        List<CourseDTO> mockContent = Collections.singletonList(createMockCourseDTO());
        Pageable pageable = PageRequest.of(0, 10);
        Page<CourseDTO> mockPage = new PageImpl<>(mockContent, pageable, 1);

        given(courseService.searchCourses(any(CourseSearchRequest.class), eq(pageable), any()))
                .willReturn(mockPage);

        // When & Then
        mockMvc.perform(
                        get("/courses")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", CourseSortBy.LATEST.name())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.page.totalElements").value(1));

        verify(courseService, times(1)).searchCourses(any(), any(), any());
    }

    @Test
    void 단일_강좌_상세_조회_테스트() throws Exception {
        // Given
        CourseResponse mockResponse = createMockCourseResponse();
        given(courseService.searchCourse(eq(MOCK_COURSE_ID))).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(
                        get("/courses/{courseId}", MOCK_COURSE_ID)
                                .contentType(MediaType.APPLICATION_JSON.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.course.id").value(MOCK_COURSE_ID));
        //            .andExpect(jsonPath("$.data.course.rating").value(4.5));

        verify(courseService, times(1)).searchCourse(eq(MOCK_COURSE_ID));
    }

    @Test
    void 강좌_등록_테스트() throws Exception {
        // Given
        CourseSaveRequest request =
                new CourseSaveRequest(
                        "new_course",
                        "New Course Summary",
                        "New Course Description",
                        CourseLevel.BEGINNER,
                        20000,
                        null,
                        1L);
        CourseResponse mockResponse = createMockCourseResponse();

        given(courseService.saveCourse(any(CourseSaveRequest.class), any()))
                .willReturn(mockResponse);

        String content = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(post("/courses").contentType(MediaType.APPLICATION_JSON).content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.course.title").value("Mock Detailed Course"));

        verify(courseService, times(1)).saveCourse(any(), any());
    }

    @Test
    void 강좌_수정_테스트() throws Exception {
        // Given
        CourseUpdateRequest request =
                new CourseUpdateRequest(
                        "Updated Title",
                        "Updated Summary",
                        "Updated Description",
                        CourseLevel.INTERMEDIATE,
                        30000,
                        null,
                        2L);
        CourseDTO mockResponse = createMockCourseDTO(); // DTO 반환 가정

        given(courseService.updateCourse(any(Long.class), any(CourseUpdateRequest.class), any()))
                .willReturn(mockResponse);

        String content = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(
                        put("/courses/{courseId}", MOCK_COURSE_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Mock Course Title"));

        verify(courseService, times(1)).updateCourse(eq(MOCK_COURSE_ID), any(), any());
    }

    @Test
    void 강좌_삭제_테스트() throws Exception {
        // Given
        given(courseService.deleteCourse(eq(MOCK_COURSE_ID), any())).willReturn(true);

        // When & Then
        mockMvc.perform(
                        delete("/courses/{courseId}", MOCK_COURSE_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("강좌 삭제 성공"))
                .andExpect(jsonPath("$.data").value(true));

        verify(courseService, times(1)).deleteCourse(eq(MOCK_COURSE_ID), any());
    }
}
