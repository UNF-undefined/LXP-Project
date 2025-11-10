package com.example.projectlxp.lecture.service.impl;

import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.controller.dto.UploadFileInfoDTO;
import com.example.projectlxp.lecture.controller.dto.response.LectureCreateResponseDTO;
import com.example.projectlxp.lecture.entity.Lecture;
import com.example.projectlxp.lecture.entity.LectureType;
import com.example.projectlxp.lecture.repository.LectureRepository;
import com.example.projectlxp.lecture.service.LectureService;
import com.example.projectlxp.section.entity.Section;
import com.example.projectlxp.section.repository.SectionRepository;

@Service
public class LectureServiceImpl implements LectureService {

    private LectureRepository lectureRepository;
    private SectionRepository sectionRepository;

    @Autowired
    public LectureServiceImpl(
            LectureRepository lectureRepository, SectionRepository sectionRepository) {
        this.lectureRepository = lectureRepository;
        this.sectionRepository = sectionRepository;
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
                        .orElseThrow(() -> new CustomBusinessException("없는 세션입니다."));

        // check who create this lecture
        System.out.println(findSection.getCourse().getInstructor().getId());
        if (!findSection.getCourse().getInstructor().getId().equals(userId)) {
            throw new CustomBusinessException("강의 등록 권한이 없습니다.");
        }

        // convert File Info DTO
        UploadFileInfoDTO fileInfo = getFileInfo(file);

        // create Lecture
        Lecture newLecture =
                Lecture.createLecture(
                        title,
                        fileInfo.fileType,
                        orderNo,
                        fileInfo.fileURL,
                        findSection,
                        fileInfo.videoDuration);

        Lecture savedLecture = lectureRepository.save(newLecture);

        // convert DTO & return
        return new LectureCreateResponseDTO(
                savedLecture.getId(),
                savedLecture.getTitle(),
                savedLecture.getType(),
                savedLecture.getOrderNo());
    }

    private UploadFileInfoDTO getFileInfo(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
        String duration = null;

        LectureType type;
        if (fileExt.equals("mp4")) {
            type = LectureType.VIDEO;
        } else if (fileExt.equals("pdf")) {
            type = LectureType.DOCUMENT;
        } else {
            throw new CustomBusinessException("업로드 파일의 형식은 pdf 혹은 mp4만 가능합니다.");
        }

        if (type.equals(LectureType.VIDEO)) {
            duration = getVideoDuration(file);
        }

        // TODO : 임의의 파일 스토리지 저장소
        String fileURL = "s3:/test/" + fileName;
        return new UploadFileInfoDTO(fileURL, type, duration);
    }

    private String getVideoDuration(MultipartFile file) throws Exception {
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        try (InputStream inputStream = file.getInputStream()) {
            parser.parse(inputStream, new BodyContentHandler(), metadata, context);
        }

        // TODO : 비디오마다 가지고 있는 메타 정보가 달라서 길이를 추출하지 못함.
        // 그렇다고 ffempg 라이브러리를 사용하자고 하니 라이브러라 하나가 200Mb된다고 함.(오버엔지니어링)
        String duration = metadata.get("xmpDM:duration"); // 초 단위
        if (duration != null) {
            return duration;
        } else {
            throw new CustomBusinessException("비디오 길이를 가져올 수 없습니다.");
        }
    }
}
