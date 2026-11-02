package com.eventcloud.service;
import org.springframework.web.multipart.MultipartFile;
public interface PosterStorageService {
    String store(MultipartFile file, String contentType) throws Exception;
}
