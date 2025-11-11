package com.example.projectlxp.lecture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectlxp.content.service.ContentService;
import com.example.projectlxp.content.service.dto.UploadFileInfoDTO;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.controller.dto.response.LectureCreateResponseDTO;
import com.example.projectlxp.lecture.entity.Lecture;
import com.example.projectlxp.lecture.repository.LectureRepository;
import com.example.projectlxp.lecture.service.LectureService;
import com.example.projectlxp.lecture.service.validator.LectureValidator;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.section.repository.SectionRepository;

@Service
public class LectureServiceImpl implements LectureService {

    private LectureRepository lectureRepository;
    private SectionRepository sectionRepository;
    private ContentService contentService;
    private LectureValidator lectureValidator;

    @Autowired
    public LectureServiceImpl(
            LectureRepository lectureRepository,
            SectionRepository sectionRepository,
            ContentService contentService,
            LectureValidator lectureValidator) {
        this.lectureRepository = lectureRepository;
        this.sectionRepository = sectionRepository;
        this.contentService = contentService;
        this.lectureValidator = lectureValidator;
    }

    @Override
    @Transactional
    public LectureCreateResponseDTO registerLecture(
            Long userId, Long sectionId, String title, int orderNo, MultipartFile file)
            throws Exception {

        // find section
        Section findSection =
                sectionRepository
                        .findByIdWithCourseAndInstructor(sectionId)
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "없는 세션입니다.", HttpStatus.NOT_FOUND));

        // check who create this lecture
        lectureValidator.validateLectureAuthority(
                findSection.getCourse().getInstructor().getId(), userId);

        // convert File Info DTO
        UploadFileInfoDTO fileInfo = contentService.uploadFile(file);

        // create Lecture
        Lecture newLecture =
                Lecture.createLecture(
                        title,
                        fileInfo.fileType(),
                        orderNo,
                        fileInfo.fileURL(),
                        findSection,
                        fileInfo.videoDuration());

        Lecture savedLecture = lectureRepository.save(newLecture);

        // convert DTO & return
        return new LectureCreateResponseDTO(
                savedLecture.getId(),
                savedLecture.getTitle(),
                savedLecture.getType(),
                savedLecture.getOrderNo());
    }
}
