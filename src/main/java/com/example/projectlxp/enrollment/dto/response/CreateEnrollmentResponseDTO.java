package com.example.projectlxp.enrollment.dto.response;

import java.time.LocalDateTime;

import com.example.projectlxp.enrollment.entity.Enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollmentResponseDTO {
    private Long enrollmentId;
    private Long userId;
    private String userName;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime enrolledAt;

    public static CreateEnrollmentResponseDTO from(Enrollment enrollment) {
        return CreateEnrollmentResponseDTO.builder()
                .enrollmentId(enrollment.getId())
                .userId(enrollment.getUser().getId())
                .userName(enrollment.getUser().getName())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .enrolledAt(enrollment.getCreatedAt())
                .build();
    }
}
