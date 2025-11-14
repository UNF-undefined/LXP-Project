package com.example.projectlxp.course.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.course.dto.CourseDTO;
import com.example.projectlxp.course.dto.request.CourseSaveRequest;
import com.example.projectlxp.course.dto.request.CourseSearchRequest;
import com.example.projectlxp.course.dto.request.CourseUpdateRequest;
import com.example.projectlxp.course.dto.response.CourseResponse;
import com.example.projectlxp.course.service.CourseService;
import com.example.projectlxp.global.annotation.CurrentUserId;
import com.example.projectlxp.global.dto.BaseResponse;
import com.example.projectlxp.global.dto.PageDTO;
import com.example.projectlxp.global.dto.PageResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Course")
@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public PageResponse<List<CourseDTO>> searchAll(
            @PageableDefault Pageable pageable, @ModelAttribute CourseSearchRequest request) {
        Page<CourseDTO> data = courseService.searchCourses(request, pageable);
        return PageResponse.success(data.toList(), PageDTO.of(data));
    }

    @GetMapping("/{courseId}")
    public BaseResponse<CourseResponse> search(@PathVariable Long courseId) {
        return BaseResponse.success(courseService.searchCourse(courseId));
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public BaseResponse<CourseResponse> registerCourse(
            @RequestBody @Valid CourseSaveRequest request, @CurrentUserId Long userId) {
        return BaseResponse.success(courseService.saveCourse(request, userId));
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public BaseResponse<CourseDTO> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseUpdateRequest request,
            @CurrentUserId Long userId) {
        return BaseResponse.success(courseService.updateCourse(courseId, request, userId));
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public BaseResponse<Boolean> deleteCourse(
            @PathVariable Long courseId, @CurrentUserId Long userId) {
        return BaseResponse.success("강좌 삭제 성공", courseService.deleteCourse(courseId, userId));
    }
}
