package com.example.projectlxp.enrollment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LectureProgressTest {
    @Test
    @DisplayName("강의를 완료(complete) 처리하면, 상태가 true로 변경되고 마지막 접근 시간이 기록된다.")
    void complete() {
        // given
        LectureProgress progress = LectureProgress.builder().enrollment(null).lecture(null).build();

        assertThat(progress.isCompleted()).isFalse();
        assertThat(progress.getLastAccessedAt()).isNull();

        LocalDateTime testStartTime = LocalDateTime.now();

        // when
        progress.complete();

        // then
        assertThat(progress.isCompleted()).isTrue();
        assertThat(progress.getLastAccessedAt()).isNotNull().isAfterOrEqualTo(testStartTime);
    }
}
