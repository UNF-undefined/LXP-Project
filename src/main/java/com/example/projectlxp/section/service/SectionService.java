package com.example.projectlxp.section.service;

import com.example.projectlxp.section.controller.dto.response.SectionCreateResponseDTO;

public interface SectionService {

    /**
     * 섹션을 생성합니다.
     *
     * @param courseId 강좌(course)의 ID값
     * @param title 섹션의 제목
     * @param orderNo 섹션의 순서
     * @return SectionCreateResponseDTO
     */
    public SectionCreateResponseDTO registerSection(Long courseId, String title, int orderNo);
}
