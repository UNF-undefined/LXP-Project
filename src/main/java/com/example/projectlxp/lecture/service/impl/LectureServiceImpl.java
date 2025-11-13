package com.example.projectlxp.lecture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.content.service.ContentService;
import com.example.projectlxp.content.service.dto.UploadFileInfoDTO;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.controller.dto.LectureCreateDTO;
import com.example.projectlxp.lecture.controller.dto.LectureDeleteDTO;
import com.example.projectlxp.lecture.controller.dto.LectureDetailDTO;
import com.example.projectlxp.lecture.controller.dto.LectureModifyDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureCreateResponseDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureGetDetailResponseDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureUpdateResponseDTO;
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
    public LectureCreateResponseDTO registerLecture(LectureCreateDTO lectureCreate)
            throws Exception {

        // validate create lecture dto
        lectureValidator.validateLectureCreateInfo(lectureCreate);

        // find section
        Section findSection =
                sectionRepository
                        .findByIdWithCourseAndInstructor(lectureCreate.sectionId())
                        .orElseThrow(
                                () ->
                                        new CustomBusinessException(
                                                "섹션이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        // check who create this lecture
        lectureValidator.validateLectureAuthority(
                findSection.getCourse().getInstructor().getId(), lectureCreate.userId());

        // convert File Info DTO
        UploadFileInfoDTO fileInfo = contentService.uploadFile(lectureCreate.file());

        // create Lecture
        Lecture newLecture =
                Lecture.createLecture(
                        lectureCreate.title(),
                        fileInfo.fileType(),
                        lectureCreate.orderNo(),
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

    @Override
    @Transactional
    public LectureUpdateResponseDTO modifyLecture(LectureModifyDTO modifyInfo) {
        // Validate DTO
        lectureValidator.validateLectureModifyInfo(modifyInfo);

        // find Lecture
        Lecture findLecture = findLectureAndException(modifyInfo.lectureId());

        // validate Lecture authority
        lectureValidator.validateLectureAuthority(
                findLecture.getSection().getCourse().getInstructor().getId(), modifyInfo.userId());

        // setup info
        Long sectionId = findLecture.getSection().getId();
        int oldOrderNo = findLecture.getOrderNo();
        int newOrderNo = modifyInfo.orderNo();

        // orderNo reorder
        if (newOrderNo < oldOrderNo) {
            lectureRepository.incrementOrderBetween(sectionId, oldOrderNo, newOrderNo);
        } else {
            lectureRepository.decrementOrderBetween(sectionId, oldOrderNo, newOrderNo);
        }

        // TODO : section을 변경하면 옮기는 section 내부에서의 orderNo도 변경되어야 해서 현재 보류
        // update Lecture
        Lecture updatedLecture = findLecture.updateLecture(modifyInfo.title(), newOrderNo);

        // convertDTO and return
        return new LectureUpdateResponseDTO(
                updatedLecture.getSection().getId(),
                updatedLecture.getTitle(),
                updatedLecture.getOrderNo());
    }

    @Override
    @Transactional
    public void removeLecture(LectureDeleteDTO deleteInfo) {
        // set info
        Long userId = deleteInfo.userId();
        Long lectureId = deleteInfo.lectureId();

        // find Lecture
        Lecture findLecture = findLectureAndException(lectureId);

        // validate lecture authority
        lectureValidator.validateLectureAuthority(
                findLecture.getSection().getCourse().getInstructor().getId(), userId);

        // soft delete lecture
        lectureRepository.delete(findLecture);

        // order_no reorder
        lectureRepository.decrementOrderAfterDelete(
                findLecture.getSection().getId(), findLecture.getOrderNo());
    }

    @Override
    @Transactional
    public LectureGetDetailResponseDTO getLectureDetail(LectureDetailDTO detailInfo) {
        // set info
        Long userId = detailInfo.userId();
        Long lectureId = detailInfo.lectureId();

        // find Lecture
        Lecture findLecture = findLectureDetail(lectureId);

        // validate lecture authority
        lectureValidator.validateLectureAuthority(
                findLecture.getSection().getCourse().getInstructor().getId(), userId);

        // convertDTO and return
        return new LectureGetDetailResponseDTO(
                lectureId,
                findLecture.getTitle(),
                findLecture.getFile(),
                findLecture.getType(),
                findLecture.getOrderNo());
    }

    private Lecture findLectureAndException(Long lectureId) {
        return lectureRepository
                .findById(lectureId)
                .orElseThrow(
                        () -> new CustomBusinessException("강의를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    private Lecture findLectureDetail(Long lectureId) {
        return lectureRepository
                .findDetailById(lectureId)
                .orElseThrow(
                        () -> new CustomBusinessException("강의를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}
