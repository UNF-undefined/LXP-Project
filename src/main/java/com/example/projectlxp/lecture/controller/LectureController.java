package com.example.projectlxp.lecture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.global.annotation.CurrentUserId;
import com.example.projectlxp.global.dto.BaseResponse;
import com.example.projectlxp.lecture.controller.dto.LectureCreateDTO;
import com.example.projectlxp.lecture.controller.dto.LectureDeleteDTO;
import com.example.projectlxp.lecture.controller.dto.LectureDetailDTO;
import com.example.projectlxp.lecture.controller.dto.LectureModifyDTO;
import com.example.projectlxp.lecture.controller.dto.request.LectureCreateRequestDTO;
import com.example.projectlxp.lecture.controller.dto.request.LectureUpdateRequestDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureCreateResponseDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureGetDetailResponseDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureUpdateResponseDTO;
import com.example.projectlxp.lecture.service.LectureService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Lecture")
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
            @ModelAttribute LectureCreateRequestDTO request, @CurrentUserId Long userId)
            throws Exception {

        // convert to DTO
        LectureCreateDTO lectureCreate =
                new LectureCreateDTO(
                        userId,
                        request.sectionId(),
                        request.title(),
                        request.orderNo(),
                        request.file());

        LectureCreateResponseDTO response = lectureService.registerLecture(lectureCreate);
        return BaseResponse.success(response);
    }

    @PatchMapping("/{lectureId}")
    public BaseResponse<LectureUpdateResponseDTO> updateLecture(
            @PathVariable(name = "lectureId") Long lectureId,
            @RequestBody LectureUpdateRequestDTO request,
            @CurrentUserId Long userId) {

        // convert to LectureModifyDTO
        LectureModifyDTO modifyInfo =
                new LectureModifyDTO(userId, lectureId, request.title(), request.orderNo());

        LectureUpdateResponseDTO response = lectureService.modifyLecture(modifyInfo);

        return BaseResponse.success(response);
    }

    @DeleteMapping("/{lectureId}")
    public BaseResponse<?> deleteLecture(
            @PathVariable(name = "lectureId") Long lectureId, @CurrentUserId Long userId) {
        // convert to DTO
        LectureDeleteDTO deleteInfo = new LectureDeleteDTO(userId, lectureId);
        lectureService.removeLecture(deleteInfo);
        return BaseResponse.success("Deleted Lecture");
    }

    @GetMapping("/{lectureId}")
    public BaseResponse<LectureGetDetailResponseDTO> getLecture(
            @PathVariable(name = "lectureId") Long lectureId, @CurrentUserId Long userId) {

        LectureDetailDTO lectureInfo = new LectureDetailDTO(userId, lectureId);

        LectureGetDetailResponseDTO response = lectureService.getLectureDetail(lectureInfo);
        return BaseResponse.success(response);
    }
}
