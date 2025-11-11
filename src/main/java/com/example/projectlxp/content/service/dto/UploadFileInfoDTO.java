package com.example.projectlxp.content.service.dto;

import com.example.projectlxp.lecture.entity.LectureType;

public record UploadFileInfoDTO(String fileURL, LectureType fileType, String videoDuration) {}
