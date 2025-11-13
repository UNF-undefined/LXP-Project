package com.example.projectlxp.lecture.service;

import com.example.projectlxp.lecture.controller.dto.LectureCreateDTO;
import com.example.projectlxp.lecture.controller.dto.LectureDeleteDTO;
import com.example.projectlxp.lecture.controller.dto.LectureDetailDTO;
import com.example.projectlxp.lecture.controller.dto.LectureModifyDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureCreateResponseDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureGetDetailResponseDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureUpdateResponseDTO;

public interface LectureService {

    /**
     * 강의를 등록합니다.
     *
     * @param lectureCreateDTO 강의(Lecture)를 생성하는데 필요한 정보들
     * @return LectureCreateResponseDTO
     */
    LectureCreateResponseDTO registerLecture(LectureCreateDTO lectureCreateDTO) throws Exception;

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

    /**
     * 강의의 디테일 정보를 조회합니다.
     *
     * @param detailInfo 강의의 세부 정보를 조회하기 위한 정보
     */
    LectureGetDetailResponseDTO getLectureDetail(LectureDetailDTO detailInfo);
}
