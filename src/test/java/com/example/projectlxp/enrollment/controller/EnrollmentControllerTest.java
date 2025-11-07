package com.example.projectlxp.enrollment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.projectlxp.enrollment.dto.EnrollmentResponseDTO;
import com.example.projectlxp.enrollment.service.EnrollmentService;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockitoBean private EnrollmentService enrollmentService;

    @DisplayName("강좌 수강신청 API 호출에 성공한다.")
    @Test
    void enrollCourse_Success() throws Exception {
        // given
        long courseId = 1L;
        long userId = 1L;
        given(enrollmentService.enrollCourse(any(Long.class), any(Long.class)))
                .willReturn(
                        EnrollmentResponseDTO.builder()
                                .enrollmentId(1L)
                                .userId(1L)
                                .userName("testUser")
                                .courseId(1L)
                                .courseTitle("Java Programming")
                                .enrolledAt(LocalDateTime.now())
                                .build());

        /// when // then
        mockMvc.perform(
                        post("/courses/" + courseId + "/enrollments?userId=" + userId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("수강신청이 성공적으로 완료되었습니다."));
    }
}
