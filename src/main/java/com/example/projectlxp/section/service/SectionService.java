package com.example.projectlxp.section.service;

import com.example.projectlxp.section.controller.dto.response.SectionCreateResponseDTO;
import com.example.projectlxp.section.controller.dto.response.SectionUpdateResponseDTO;

public interface SectionService {

    /**
     * 섹션을 생성합니다.
     *
     * @param userId 유저(user)의 ID값
     * @param courseId 강좌(course)의 ID값
     * @param title 섹션의 제목
     * @param orderNo 섹션의 순서
     * @return SectionCreateResponseDTO
     */
    SectionCreateResponseDTO registerSection(Long userId, Long courseId, String title, int orderNo);

    /**
     * 섹션을 업데이트 합니다.
     *
     * @param userId 유저(User)의 ID값
     * @param sectionId 섹션(Section)의 ID값
     * @param title 섹션의 제목
     * @param orderNo 섹션의 순서
     * @return SectionUpdateResponseDTO
     */
    SectionUpdateResponseDTO modifySection(Long userId, Long sectionId, String title, int orderNo);

    /**
     * 섹션을 삭제합니다. - 섹션이 삭제되면 관련 Lecture도 삭제됩니다. - 중간 섹션을 삭제하면 orderNo가 재정렬 됩니다.
     *
     * @param userId 유저(User)의 ID값
     * @param sectionId 섹션(Section)의 ID값
     */
    void removeSection(Long userId, Long sectionId);
}
