package com.example.projectlxp.enrollment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnrollmentTest {

    @Test
    @DisplayName("완료된 강의 수를 새 값으로 업데이트한다.")
    void updateCompletedLectureCount() {
        Enrollment enrollment = Enrollment.create(null, null, 10L, false);

        assertThat(enrollment.getCompletedLectureCount()).isEqualTo(0);
        int newCompletedCount = 7;

        // when
        enrollment.updateCompletedLectureCount(newCompletedCount);

        // then
        assertThat(enrollment.getCompletedLectureCount()).isEqualTo(newCompletedCount);
    }
}
