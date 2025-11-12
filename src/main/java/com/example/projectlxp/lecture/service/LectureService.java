package com.example.projectlxp.lecture.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.projectlxp.lecture.controller.dto.LectureDeleteDTO;
import com.example.projectlxp.lecture.controller.dto.LectureModifyDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureCreateResponseDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureUpdateResponseDTO;

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
    LectureCreateResponseDTO registerLecture(
            Long userId, Long sectionId, String title, int orderNo, MultipartFile file)
            throws Exception;

    /**
     * 강의의 제목, 순서, 섹션을 수정합니다.
     *
     * @param modifyInfo 변경하고자 하는 정보들
     * @return LectureUpdateResponseDTO
     */
    LectureUpdateResponseDTO modifyLecture(LectureModifyDTO modifyInfo);

    /**
     * 강의를 삭제합니다.
     *
     * @param deleteInfo 강의 삭제 시 필요한 정보들
     */
    void removeLecture(LectureDeleteDTO deleteInfo);
}
