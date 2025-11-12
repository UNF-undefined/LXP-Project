package com.example.projectlxp.enrollment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;
import com.example.projectlxp.enrollment.service.EnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WithMockUser
@ActiveProfiles("test")
@WebMvcTest(EnrollmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class EnrollmentControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockitoBean private EnrollmentService enrollmentService;

    @Autowired private ObjectMapper objectMapper;

    @DisplayName("강좌 수강신청을 성공한다.")
    @Test
    void enrollCourse_Success() throws Exception {
        // given
        Long courseId = 1L;
        Long userId = 1L;

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
                        post("/enrollments")
                                .with(csrf())
                                .param("userId", String.valueOf(userId))
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
                        post("/enrollments")
                                .param("userId", "1")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("강좌 ID는 필수 값입니다."))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("숨김 처리 상태가 수강 목록을 제외하고 내 수강 목록을 성공적으로 조회한다.")
    @Test
    void getMyCourses_Success() throws Exception {
        // given
        Long userId = 1L;

        EnrolledCourseDTO courseDTO1 = createEnrollment("Java Basics", false);
        EnrolledCourseDTO courseDTO2 = createEnrollment("Spring Framework", true);
        EnrolledCourseDTO courseDTO3 = createEnrollment("Database Essentials", false);

        // 숨김(false)만 필터링된 결과라고 가정
        List<EnrolledCourseDTO> filteredList = List.of(courseDTO1, courseDTO3);

        PagedEnrolledCourseDTO fakePageResponse =
                PagedEnrolledCourseDTO.builder()
                        .enrolledCourseDTOList(filteredList)
                        .totalPages(1)
                        .totalElements((long) filteredList.size())
                        .isFirst(true)
                        .isLast(true)
                        .build();

        given(
                        enrollmentService.getMyEnrolledCourses(
                                any(Long.class), eq(false), any(Pageable.class)))
                .willReturn(fakePageResponse);

        // when // then
        mockMvc.perform(
                        get("/enrollments/my")
                                .param("userId", String.valueOf(userId))
                                .param("page", "0")
                                .param("size", "10")
                                .param("hidden", "false")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("수강중인 강좌 목록 조회를 성공했습니다."))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(
                        jsonPath("$.data.enrolledCourseDTOList[0].courseTitle")
                                .value("Java Basics"))
                .andExpect(
                        jsonPath("$.data.enrolledCourseDTOList[1].courseTitle")
                                .value("Database Essentials"));
    }

    @WithMockUser
    @DisplayName("수강 강좌 숨김 처리 API 성공")
    @Test
    void hideEnrollment_Success() throws Exception {
        // given
        Long userId = 1L;
        Long enrollmentId = 10L;

        EnrolledCourseDTO responseDTO =
                EnrolledCourseDTO.builder()
                        .enrollmentId(enrollmentId)
                        .courseId(101L)
                        .courseTitle("Java Basics")
                        .isHidden(true)
                        .progress(0)
                        .build();

        given(enrollmentService.hideEnrollment(userId, enrollmentId)).willReturn(responseDTO);

        // when // then
        mockMvc.perform(
                        put("/enrollments/{enrollmentId}/hide", enrollmentId)
                                .with(csrf())
                                .param("userId", String.valueOf(userId))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("수강중인 강좌가 성공적으로 숨겨졌습니다."))
                .andExpect(jsonPath("$.data.enrollmentId").value(enrollmentId))
                .andExpect(jsonPath("$.data.hidden").value(true));
    }

    @WithMockUser
    @DisplayName("수강 강좌 숨김 해제 API 성공")
    @Test
    void unhideEnrollment_Success() throws Exception {
        // given
        Long userId = 1L;
        Long enrollmentId = 10L;

        EnrolledCourseDTO responseDTO =
                EnrolledCourseDTO.builder()
                        .enrollmentId(enrollmentId)
                        .courseId(101L)
                        .courseTitle("Java Basics")
                        .isHidden(false)
                        .progress(0)
                        .build();

        given(enrollmentService.unhideEnrollment(userId, enrollmentId)).willReturn(responseDTO);

        // when // then
        mockMvc.perform(
                        put("/enrollments/{enrollmentId}/unhide", enrollmentId)
                                .with(csrf())
                                .param("userId", String.valueOf(userId))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("수강중인 강좌가 성공적으로 숨김 해제되었습니다."))
                .andExpect(jsonPath("$.data.enrollmentId").value(enrollmentId))
                .andExpect(jsonPath("$.data.hidden").value(false));
    }

    private static EnrolledCourseDTO createEnrollment(String title, boolean isHidden) {
        return EnrolledCourseDTO.builder()
                .enrollmentId(1L)
                .courseId(101L)
                .courseTitle(title)
                .progress(0)
                .isHidden(isHidden)
                .build();
    }
}
