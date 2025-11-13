package com.example.projectlxp.lecture.controller.dto;

import org.springframework.web.multipart.MultipartFile;

public record LectureCreateDTO(
        Long userId, Long sectionId, String title, int orderNo, MultipartFile file) {}
