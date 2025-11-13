package com.example.projectlxp.lecture.controller.dto.response;

import com.example.projectlxp.lecture.entity.LectureType;

public record LectureGetDetailResponseDTO(
        Long lectureId, String title, String filePath, LectureType lectureType, int orderNo) {}
