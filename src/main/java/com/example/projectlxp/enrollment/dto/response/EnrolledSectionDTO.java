package com.example.projectlxp.enrollment.dto.response;

import com.example.projectlxp.section.entity.Section; // Section 엔티티 경로에 맞게 수정 필요
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledSectionDTO {
    private Long sectionId;
    private String sectionTitle;
    private List<EnrolledLectureDTO> lectures;

    public static EnrolledSectionDTO of(Section section, List<EnrolledLectureDTO> lectures) {
        return EnrolledSectionDTO.builder()
                .sectionId(section.getId())
                .sectionTitle(section.getTitle())
                .lectures(lectures)
                .build();
    }
}
