package com.example.projectlxp.section.service.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.section.entity.Section;

@Component
public class SectionValidator {

    public void validateSectionAuthority(Section section, Long userId) {
        if (section.getCourse().getInstructor().getId() != userId) {
            throw new CustomBusinessException("섹션을 관리할 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }

    public void validateSectionAuthority(Long authId, Long checkId) {
        if (authId != checkId) {
            throw new CustomBusinessException("섹션을 관리할 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }
}
