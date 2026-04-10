package com.payr.document_service.controller;

import com.payr.document_service.dto.DocumentResponseDTO;
import com.payr.document_service.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<DocumentResponseDTO> upload(
            @RequestParam("userId") Long userId,
            @RequestPart("file") MultipartFile file) {

        System.out.println("🔥 Document upload API HIT");

        return ResponseEntity.ok(documentService.uploadFile(userId, file));
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> download(@PathVariable Long documentId) {

        byte[] data = documentService.downloadFile(documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body(data);
    }
}
