package com.example.projectlxp.lecture.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public record LectureCreateRequestDTO(
        @NotNull(message = "sectionId는 필수 입력 값 입니다.") Long sectionId,
        @NotBlank(message = "강의의 제목을 입력해주세요.") String title,
        @NotNull(message = "order no는 필수 입력 값 입니다.") Integer orderNo,
        MultipartFile file) {}
