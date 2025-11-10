package com.example.projectlxp.section.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.global.dto.BaseResponse;
import com.example.projectlxp.section.controller.dto.request.SectionCreateRequestDTO;
import com.example.projectlxp.section.controller.dto.request.SectionUpdateRequestDTO;
import com.example.projectlxp.section.controller.dto.response.SectionCreateResponseDTO;
import com.example.projectlxp.section.controller.dto.response.SectionUpdateResponseDTO;
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
            @RequestBody @Valid SectionCreateRequestDTO request,
            @RequestParam(name = "userId", defaultValue = "1") Long tempUserId) {
        SectionCreateResponseDTO createdSection =
                sectionService.registerSection(
                        tempUserId, request.courseId, request.title, request.orderNo);

        return new BaseResponse<>(HttpStatus.CREATED, "Created Section.", createdSection);
    }

    @PutMapping("/{sectionId}")
    public BaseResponse<SectionUpdateResponseDTO> updateSection(
            @PathVariable(name = "sectionId") Long sectionId,
            @RequestBody @Valid SectionUpdateRequestDTO request) {

        SectionUpdateResponseDTO updatedSection =
                sectionService.modifySection(sectionId, request.title, request.orderNo);

        return BaseResponse.success(updatedSection);
    }

    @DeleteMapping("/{sectionId}")
    public BaseResponse<Void> deleteSection(@PathVariable(name = "sectionId") Long sectionId) {
        sectionService.removeSection(sectionId);
        return BaseResponse.success("Deleted Section", null);
    }
}
