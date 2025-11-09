package com.example.projectlxp.section.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.global.dto.BaseResponse;
import com.example.projectlxp.section.controller.dto.request.SectionCreateRequestDTO;
import com.example.projectlxp.section.controller.dto.response.SectionCreateResponseDTO;
import com.example.projectlxp.section.service.SectionService;

@RestController
@RequestMapping(value = "/sections")
public class SectionController {

    private final SectionService sectionService;

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public BaseResponse<SectionCreateResponseDTO> createSection(
            @RequestBody @Valid SectionCreateRequestDTO request) {
        try {
            SectionCreateResponseDTO createdSection =
                    sectionService.registerSection(
                            request.courseId, request.title, request.orderNo);

            return new BaseResponse<>(HttpStatus.CREATED, "Created Section.", createdSection);
        } catch (IllegalArgumentException e) {
            return BaseResponse.error(HttpStatus.NOT_FOUND, "Course를 찾을 수 없습니다.");
        } catch (IllegalStateException e) {
            return BaseResponse.error(HttpStatus.BAD_REQUEST, "Section의 순서가 동일합니다.");
        }
    }
}
