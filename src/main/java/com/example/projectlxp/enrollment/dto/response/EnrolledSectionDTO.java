package com.example.projectlxp.enrollment.dto.response;

import java.util.List;

import com.example.projectlxp.section.entity.Section;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
