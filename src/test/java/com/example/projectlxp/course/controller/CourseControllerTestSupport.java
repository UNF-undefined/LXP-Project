package com.example.projectlxp.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.projectlxp.course.service.CourseService;
import com.example.projectlxp.global.config.SecurityConfig;
import com.example.projectlxp.global.jwt.JwtAuthenticationFilter;
import com.example.projectlxp.global.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@WithMockUser(
        username = "1",
        authorities = {"INSTRUCTOR"})
@Import(CourseControllerTest.TestSecurityConfig.class)
@WebMvcTest(
        controllers = CourseController.class,
        excludeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = {JwtAuthenticationFilter.class, SecurityConfig.class})
        },
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class CourseControllerTestSupport {

    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;

    @MockitoBean protected CourseService courseService;

    @MockitoBean protected JwtTokenProvider jwtTokenProvider;
}
