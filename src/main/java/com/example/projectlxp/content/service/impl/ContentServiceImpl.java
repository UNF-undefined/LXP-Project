package com.example.projectlxp.content.service.impl;

import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectlxp.content.service.ContentService;
import com.example.projectlxp.global.error.CustomBusinessException;
import com.example.projectlxp.lecture.controller.dto.UploadFileInfoDTO;
import com.example.projectlxp.lecture.entity.LectureType;

@Service
public class ContentServiceImpl implements ContentService {

    // TODO : 파일 스토리지 저장소 의존성 추가

    @Override
    @Transactional
    public UploadFileInfoDTO uploadFile(MultipartFile file) throws Exception {

        // 업로드한 파일의 정보를 전달하기 위한 DTO 생성 프로세스
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
        // 이 부분에서 파일 스토리지에 저장한다.
        String fileURL = "s3:/test/" + fileName;

        // return
        return new UploadFileInfoDTO(fileURL, type, duration);
    }

    /**
     * Video 파일의 영상 시간을 추출합니다.
     *
     * @param file form 데이터로 받은 file
     */
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
