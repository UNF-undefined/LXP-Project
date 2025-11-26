package com.example.projectlxp.enrollment.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.example.projectlxp.ControllerTestSupport;
import com.example.projectlxp.global.annotation.WithMockUserId;
import com.example.projectlxp.global.jwt.JwtAuthenticationFilter;

@WebMvcTest(
        controllers = LectureProgressController.class,
        excludeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = JwtAuthenticationFilter.class)
        })
class LectureProgressControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("강의 시청 시작 - 성공")
    @WithMockUserId
    void startLecture_Success() throws Exception {
        // given
        Long userId = 1L; // @WithMockUserId 값과 일치
        Long lectureId = 10L;

        // 서비스의 void 메서드가 호출될 것을 설정
        doNothing().when(lectureProgressService).markLectureAsStarted(userId, lectureId);

        // when & then
        mockMvc.perform(
                        post("/lectures/{lectureId}/start", lectureId)
                                .with(csrf())) // Spring Security CSRF 토큰
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("강의 시작 처리가 성공적으로 이루어졌습니다."))
                .andExpect(jsonPath("$.data").isEmpty()); // data는 null

        // verify
        // 서비스 메서드가 정확한 인자들로 1번 호출되었는지 검증
        verify(lectureProgressService).markLectureAsStarted(userId, lectureId);
    }

    @Test
    @DisplayName("강의 시청 완료 - 성공")
    @WithMockUserId
    void completeLecture_Success() throws Exception {
        // given
        Long userId = 1L; // @WithMockUserId 값과 일치
        Long lectureId = 20L;

        doNothing().when(lectureProgressService).markLectureAsComplete(userId, lectureId);

        // when & then
        mockMvc.perform(post("/lectures/{lectureId}/complete", lectureId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("강의 완료 처리가 성공적으로 이루어졌습니다."))
                .andExpect(jsonPath("$.data").isEmpty());

        // verify
        verify(lectureProgressService).markLectureAsComplete(userId, lectureId);
    }
}
