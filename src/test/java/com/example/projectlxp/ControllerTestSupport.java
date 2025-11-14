package com.example.projectlxp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.projectlxp.enrollment.controller.EnrollmentController;
import com.example.projectlxp.enrollment.controller.LectureProgressController;
import com.example.projectlxp.enrollment.service.EnrollmentService;
import com.example.projectlxp.enrollment.service.LectureProgressService;
import com.example.projectlxp.global.jwt.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
        controllers = {EnrollmentController.class, LectureProgressController.class},
        excludeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = JwtAuthenticationFilter.class)
        })
public abstract class ControllerTestSupport {
    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;

    @MockitoBean protected EnrollmentService enrollmentService;

    @MockitoBean protected UserDetailsService userDetailsService;

    @MockitoBean protected LectureProgressService lectureProgressService;
}
