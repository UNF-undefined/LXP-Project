package com.example.projectlxp.lecture.controller.dto;

import com.example.projectlxp.lecture.entity.LectureType;

public class UploadFileInfoDTO {
    public String fileURL;

    public LectureType fileType;

    public String videoDuration;

    public UploadFileInfoDTO(String fileURL, LectureType fileExt, String videoDuration) {
        this.fileURL = fileURL;
        this.fileType = fileExt;
        this.videoDuration = videoDuration;
    }
}
