package com.example.projectlxp.enrollment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;
import com.example.projectlxp.enrollment.service.EnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockitoBean private EnrollmentService enrollmentService;

    @Autowired private ObjectMapper objectMapper;

    @DisplayName("강좌 수강신청 API 호출에 성공한다.")
    @Test
    void enrollCourse_Success() throws Exception {
        // given
        long courseId = 1L;
        long userId = 1L;

        CreateEnrollmentRequestDTO request =
                CreateEnrollmentRequestDTO.builder().courseId(courseId).build();

        given(
                        enrollmentService.enrollCourse(
                                any(Long.class), any(CreateEnrollmentRequestDTO.class)))
                .willReturn(
                        CreateEnrollmentResponseDTO.builder()
                                .enrollmentId(1L)
                                .userId(1L)
                                .userName("testUser")
                                .courseId(1L)
                                .courseTitle("Java Programming")
                                .enrolledAt(LocalDateTime.now())
                                .build());

        /// when // then
        mockMvc.perform(
                        post("/enrollments?userId=" + userId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("수강신청이 성공적으로 완료되었습니다."));
    }

    @DisplayName("수강 신청을 할 때, 강좌 아이디는 필수 값이다.")
    @Test
    void createOrderWithEmptyProductNumbers() throws Exception {
        // given
        CreateEnrollmentRequestDTO request = CreateEnrollmentRequestDTO.builder().build();

        // when // then
        mockMvc.perform(
                        post("/enrollments?userId=1")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("내 수강 목록을 성공적으로 조회한다.")
    @Test
    void getMyCourses_Success() throws Exception {
        // given
        Long userId = 1L;
        EnrolledCourseDTO courseDTO =
                EnrolledCourseDTO.builder()
                        .enrollmentId(1L)
                        .courseId(101L)
                        .courseTitle("Java Basics")
                        .progress(0)
                        .build();

        List<EnrolledCourseDTO> dtoList = List.of(courseDTO);
        PagedEnrolledCourseDTO fakePageResponse =
                PagedEnrolledCourseDTO.builder()
                        .enrolledCourseDTOList(dtoList)
                        .totalPages(1)
                        .totalElements(1L)
                        .isFirst(true)
                        .isLast(true)
                        .build();

        given(enrollmentService.getMyEnrolledCourses(any(Long.class), any(Pageable.class)))
                .willReturn(fakePageResponse);

        // when // then
        mockMvc.perform(
                        get("/enrollments/my")
                                .param("userId", String.valueOf(userId))
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("수강중인 강좌 목록 조회를 성공했습니다."))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(
                        jsonPath("$.data.enrolledCourseDTOList[0].courseTitle")
                                .value("Java Basics"));
    }
}
