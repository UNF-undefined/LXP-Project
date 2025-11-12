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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.projectlxp.ControllerTestSupport;
import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDetailDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledLectureDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledSectionDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;

class EnrollmentControllerTest extends ControllerTestSupport {

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

    @WithMockUser // Spring Security가 적용된 엔드포인트일 경우를 대비해 추가
    @DisplayName("수강중인 강좌의 상세 정보를 조회한다.")
    @Test
    void getMyCourseDetail_Success() throws Exception {
        // given
        Long userId = 1L;
        Long enrollmentId = 1L;

        EnrolledLectureDTO lecture1 =
                EnrolledLectureDTO.builder()
                        .lectureId(101L)
                        .title("1-1강: DI와 IoC")
                        .completed(true)
                        .build();

        EnrolledLectureDTO lecture2 =
                EnrolledLectureDTO.builder()
                        .lectureId(102L)
                        .title("1-2강: AOP란?")
                        .completed(false)
                        .build();

        EnrolledSectionDTO section1 =
                EnrolledSectionDTO.builder()
                        .sectionId(201L)
                        .sectionTitle("섹션 1: 스프링 핵심 원리")
                        .lectures(List.of(lecture1, lecture2))
                        .build();

        EnrolledCourseDetailDTO responseDTO =
                EnrolledCourseDetailDTO.builder()
                        .enrollmentId(enrollmentId)
                        .enrolledAt(LocalDateTime.now()) // 실제 값은 중요하지 않음
                        .courseId(301L)
                        .courseTitle("스프링 부트 완벽 가이드")
                        .instructorName("김강사")
                        .courseThumbnailUrl("http://path.to/thumb.jpg")
                        .completionRate(50.0)
                        .sections(List.of(section1))
                        .build();

        given(enrollmentService.getMyEnrolledCourseDetail(userId, enrollmentId))
                .willReturn(responseDTO);

        // when // then
        mockMvc.perform(
                        get("/enrollments/{enrollmentId}/detail", enrollmentId)
                                .param("userId", String.valueOf(userId))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("수강중인 강좌 상세 조회를 성공했습니다."))
                .andExpect(jsonPath("$.data.enrollmentId").value(enrollmentId))
                .andExpect(jsonPath("$.data.courseTitle").value("스프링 부트 완벽 가이드"))
                .andExpect(jsonPath("$.data.instructorName").value("김강사"))
                .andExpect(jsonPath("$.data.completionRate").value(50.0))
                .andExpect(jsonPath("$.data.sections[0].sectionTitle").value("섹션 1: 스프링 핵심 원리"))
                .andExpect(jsonPath("$.data.sections[0].lectures[0].title").value("1-1강: DI와 IoC"))
                .andExpect(jsonPath("$.data.sections[0].lectures[0].completed").value(true))
                .andExpect(jsonPath("$.data.sections[0].lectures[1].title").value("1-2강: AOP란?"))
                .andExpect(jsonPath("$.data.sections[0].lectures[1].completed").value(false));
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
                .isHidden(isHidden)
                .build();
    }
}
