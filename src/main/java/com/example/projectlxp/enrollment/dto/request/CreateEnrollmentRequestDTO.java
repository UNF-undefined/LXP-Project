package com.example.projectlxp.enrollment.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollmentRequestDTO {
    @NotNull(message = "강좌 ID는 필수 값입니다.")
    private Long courseId;
}
