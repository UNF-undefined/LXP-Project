package com.example.projectlxp.lecture.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.projectlxp.lecture.controller.dto.response.LectureCreateResponseDTO;

public interface LectureService {

    /**
     * 강의를 등록합니다.
     *
     * @param userId 유저(User)의 ID값
     * @param sectionId 섹션(Section)의 ID 값
     * @param title 강의의 제목
     * @param orderNo 강의의 순서
     * @param file 업로드하는 파일
     * @return LectureCreateResponseDTO
     */
    public LectureCreateResponseDTO registerLecture(
            Long userId, Long sectionId, String title, int orderNo, MultipartFile file)
            throws Exception;
}
