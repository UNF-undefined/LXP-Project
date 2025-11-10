package com.example.projectlxp.section.service;

import com.example.projectlxp.section.controller.dto.response.SectionCreateResponseDTO;
import com.example.projectlxp.section.controller.dto.response.SectionUpdateResponseDTO;

public interface SectionService {

    /**
     * 섹션을 생성합니다.
     *
     * @param courseId 강좌(course)의 ID값
     * @param title 섹션의 제목
     * @param orderNo 섹션의 순서
     * @return SectionCreateResponseDTO
     */
    SectionCreateResponseDTO registerSection(Long courseId, String title, int orderNo);

    /**
     * 섹션을 업데이트 합니다.
     *
     * @param sectionId 섹션(Section)의 ID값
     * @param title 섹션의 제목
     * @param orderNo 섹션의 순서
     * @return SectionUpdateResponseDTO
     */
    SectionUpdateResponseDTO modifySection(Long sectionId, String title, int orderNo);
}
