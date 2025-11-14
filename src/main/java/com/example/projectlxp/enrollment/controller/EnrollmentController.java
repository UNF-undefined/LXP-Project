package com.example.projectlxp.enrollment.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDTO;
import com.example.projectlxp.enrollment.dto.response.EnrolledCourseDetailDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;
import com.example.projectlxp.enrollment.service.EnrollmentService;
import com.example.projectlxp.global.annotation.CurrentUserId;
import com.example.projectlxp.global.dto.BaseResponse;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public BaseResponse<CreateEnrollmentResponseDTO> enrollCourse(
            @CurrentUserId Long userId, @Valid @RequestBody CreateEnrollmentRequestDTO requestDTO) {
        CreateEnrollmentResponseDTO createEnrollmentResponseDTO =
                enrollmentService.enrollCourse(userId, requestDTO);
        return BaseResponse.success("수강신청이 성공적으로 완료되었습니다.", createEnrollmentResponseDTO);
    }

    @GetMapping("/{enrollmentId}/detail")
    public BaseResponse<EnrolledCourseDetailDTO> getMyCourseDetail(
            @CurrentUserId Long userId, @PathVariable Long enrollmentId) {
        EnrolledCourseDetailDTO enrolledCourseDetailDTO =
                enrollmentService.getMyEnrolledCourseDetail(userId, enrollmentId);
        return BaseResponse.success("수강중인 강좌 상세 조회를 성공했습니다.", enrolledCourseDetailDTO);
    }

    @GetMapping("/my")
    public BaseResponse<PagedEnrolledCourseDTO> getMyCourses(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "false") Boolean hidden,
            @PageableDefault Pageable pageable) {
        PagedEnrolledCourseDTO pagedEnrolledCourseDTO =
                enrollmentService.getMyEnrolledCourses(userId, hidden, pageable);
        return BaseResponse.success("수강중인 강좌 목록 조회를 성공했습니다.", pagedEnrolledCourseDTO);
    }

    @PutMapping("/{enrollmentId}/hide")
    public BaseResponse<EnrolledCourseDTO> hideEnrollment(
            @CurrentUserId Long userId, @PathVariable Long enrollmentId) {
        return BaseResponse.success(
                "수강중인 강좌가 성공적으로 숨겨졌습니다.", enrollmentService.hideEnrollment(userId, enrollmentId));
    }

    @PutMapping("/{enrollmentId}/unhide")
    public BaseResponse<EnrolledCourseDTO> unhideEnrollment(
            @CurrentUserId Long userId, @PathVariable Long enrollmentId) {
        return BaseResponse.success(
                "수강중인 강좌가 성공적으로 숨김 해제되었습니다.",
                enrollmentService.unhideEnrollment(userId, enrollmentId));
    }
}
