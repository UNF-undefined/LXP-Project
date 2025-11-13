package com.example.projectlxp.lecture.service.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.controller.dto.LectureCreateDTO;
import com.example.projectlxp.lecture.controller.dto.LectureModifyDTO;

@Component
public class LectureValidator {

    public void validateLectureAuthority(Long authId, Long checkId) {
        if (!authId.equals(checkId)) {
            throw new CustomBusinessException("강의를 관리할 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }

    public void validateLectureModifyInfo(LectureModifyDTO dto) {
        if ((dto.title() == null || dto.title().isBlank()) && dto.orderNo() == 0) {
            throw new CustomBusinessException("수정할 필드가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (dto.title() != null && dto.title().isBlank()) {
            throw new CustomBusinessException("강의 제목은 비워둘 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (dto.orderNo() != 0 && dto.orderNo() <= 0) {
            throw new CustomBusinessException("강의 순서는 1 이상의 값이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateLectureCreateInfo(LectureCreateDTO dto) {
        if (dto.sectionId() == null) {
            throw new CustomBusinessException("섹션 ID는 필수 값 입니다.");
        }

        if (dto.title() == null || dto.title().isBlank()) {
            throw new CustomBusinessException("강의 제목은 비워둘 수 없습니다.");
        }

        if (dto.orderNo() != 0 && dto.orderNo() <= 0) {
            throw new CustomBusinessException("강의 순서는 1 이상의 값이어야 합니다.");
        }

        if (dto.file() == null || dto.file().isEmpty()) {
            throw new CustomBusinessException("파일을 올려주세요.");
        }
    }
}
