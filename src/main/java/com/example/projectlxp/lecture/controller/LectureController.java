package com.example.projectlxp.lecture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.global.dto.BaseResponse;
import com.example.projectlxp.lecture.controller.dto.LectureModifyDTO;
import com.example.projectlxp.lecture.controller.dto.request.LectureCreateRequestDTO;
import com.example.projectlxp.lecture.controller.dto.request.LectureUpdateRequestDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureCreateResponseDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureUpdateResponseDTO;
import com.example.projectlxp.lecture.service.LectureService;

@RestController
@RequestMapping("/lectures")
public class LectureController {
    private LectureService lectureService;

    @Autowired
    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    // TODO : Service layer에 넘길 때, DTO로 넘기는 것이 더 직관적일 수 있다 !
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

    @PatchMapping("/{lectureId}")
    public BaseResponse<LectureUpdateResponseDTO> updateLecture(
            @PathVariable(name = "lectureId") Long lectureId,
            @RequestBody LectureUpdateRequestDTO request,
            @RequestParam(name = "userId", defaultValue = "1") Long userId) {

        // convert to LectureModifyDTO
        LectureModifyDTO modifyInfo =
                new LectureModifyDTO(userId, lectureId, request.getTitle(), request.getOrderNo());

        LectureUpdateResponseDTO response = lectureService.modifyLecture(modifyInfo);

        return BaseResponse.success(response);
    }
}
