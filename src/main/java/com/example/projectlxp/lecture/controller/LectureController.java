package com.example.projectlxp.lecture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.global.dto.BaseResponse;
import com.example.projectlxp.lecture.controller.dto.request.LectureCreateRequestDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureCreateResponseDTO;
import com.example.projectlxp.lecture.service.LectureService;

@RestController
@RequestMapping("/lectures")
public class LectureController {
    private LectureService lectureService;

    @Autowired
    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @PostMapping
    public BaseResponse<LectureCreateResponseDTO> createLecture(
            @ModelAttribute LectureCreateRequestDTO request,
            @RequestParam(name = "userId", defaultValue = "1") Long userId)
            throws Exception {
        LectureCreateResponseDTO response =
                lectureService.registerLecture(
                        userId,
                        request.sectionId(),
                        request.title(),
                        request.orderNo(),
                        request.file());
        return BaseResponse.success(response);
    }
}
