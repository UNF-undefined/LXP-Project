package com.example.projectlxp.course.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.example.projectlxp.category.entity.Category;
import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.request.CourseSaveRequest;
import com.example.projectlxp.course.dto.request.CourseSearchRequest;
import com.example.projectlxp.course.dto.request.CourseUpdateRequest;
import com.example.projectlxp.course.dto.response.CourseResponse;
import com.example.projectlxp.course.entity.Course;
import com.example.projectlxp.course.entity.CourseLevel;
import com.example.projectlxp.course.entity.CourseSortBy;
import com.example.projectlxp.user.entity.User;

class CourseControllerTest extends CourseControllerTestSupport {

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

    private RequestPostProcessor instructorAuth() {
        TestUser principal = new TestUser(MOCK_USER_ID, "test@naver.com", "tester", "INSTRUCTOR");
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(
                        principal,
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_INSTRUCTOR"),
                                new SimpleGrantedAuthority("INSTRUCTOR")));
        return authentication(auth);
    }

    @Test
    void 강좌_목록_검색_및_페이징_테스트() throws Exception {
        // Given
        List<CourseDTO> mockContent = Collections.singletonList(createMockCourseDTO());
        Pageable pageable = PageRequest.of(0, 10);
        Page<CourseDTO> mockPage = new PageImpl<>(mockContent, pageable, 1);

        given(courseService.searchCourses(any(CourseSearchRequest.class), eq(pageable)))
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

        verify(courseService, times(1)).searchCourses(any(), any());
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
        String content = objectMapper.writeValueAsString(request);

        given(courseService.saveCourse(any(CourseSaveRequest.class), any()))
                .willReturn(mockResponse);

        // When & Then
        mockMvc.perform(
                        post("/courses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.course.title").value("Mock Detailed Course"));

        verify(courseService, times(1)).saveCourse(any(), any());
    }

    @Test
    @WithMockUser(
            username = "1",
            authorities = {"INSTRUCTOR"})
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
                                .content(content)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Mock Course Title"));

        verify(courseService, times(1)).updateCourse(eq(MOCK_COURSE_ID), any(), any());
    }

    @Test
    @WithMockUser(
            username = "1",
            authorities = {"INSTRUCTOR"})
    void 강좌_삭제_테스트() throws Exception {
        // Given
        given(courseService.deleteCourse(eq(MOCK_COURSE_ID), any())).willReturn(true);

        // When & Then
        mockMvc.perform(
                        delete("/courses/{courseId}", MOCK_COURSE_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("강좌 삭제 성공"))
                .andExpect(jsonPath("$.data").value(true));

        verify(courseService, times(1)).deleteCourse(eq(MOCK_COURSE_ID), any());
    }

    @TestConfiguration
    @EnableMethodSecurity
    public static class TestSecurityConfig {
        @Bean
        public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
            DefaultMethodSecurityExpressionHandler expressionHandler =
                    new DefaultMethodSecurityExpressionHandler();
            expressionHandler.setDefaultRolePrefix("");
            return expressionHandler;
        }
    }

    public class TestUser implements Serializable {
        private final Long id;
        private final String email;
        private final String name;
        private final String role;

        public TestUser(Long id, String email, String name, String role) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }
    }
}
