package com.example.projectlxp.lecture.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Setter;

@Setter
public class LectureCreateRequestDTO {

    @NotNull(message = "sectionId는 필수 입력 값 입니다.")
    public Long sectionId;

    @NotBlank(message = "강의의 제목을 입력해주세요.")
    public String title;

    @NotNull(message = "order no는 필수 입력 값 입니다.")
    public int orderNo;

    public MultipartFile file;
}
