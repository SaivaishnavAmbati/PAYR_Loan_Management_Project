package com.payr.loan_service.client;

import com.payr.loan_service.dto.DocumentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "document-service")
public interface DocumentClient {
    @PostMapping(value = "/documents/upload", consumes = "multipart/form-data")
    DocumentResponseDTO upload(
            @RequestParam Long userId,
            @RequestPart("file") MultipartFile file);
}
