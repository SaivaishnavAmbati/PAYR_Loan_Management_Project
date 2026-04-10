package com.payr.document_service.service;


import com.payr.document_service.dto.DocumentResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    DocumentResponseDTO uploadFile(Long userId, MultipartFile file);

    byte[] downloadFile(Long documentId);
}

