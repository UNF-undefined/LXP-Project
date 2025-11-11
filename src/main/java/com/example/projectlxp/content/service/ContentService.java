package com.example.projectlxp.content.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.projectlxp.content.service.dto.UploadFileInfoDTO;

public interface ContentService {

    /**
     * 강의 파일을 스토리지 저장소에 저장합니다.
     *
     * @param file form 데이터로 받은 file
     * @return UploadFileInfoDTO
     */
    UploadFileInfoDTO uploadFile(MultipartFile file) throws Exception;
}
