package com.payr.document_service.service.impl;


import com.payr.document_service.dto.DocumentResponseDTO;
import com.payr.document_service.exception.ResourceNotFoundException;
import com.payr.document_service.model.Document;
import com.payr.document_service.repository.DocumentRepository;
import com.payr.document_service.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final S3Client s3Client;
    private final DocumentRepository documentRepository;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Override
    public DocumentResponseDTO uploadFile(Long userId, MultipartFile file) {

        try {
            String s3Key = userId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            String s3Url = "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;

            Document document = Document.builder()
                    .userId(userId)
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .s3Key(s3Key)
                    .s3Url(s3Url)
                    .uploadedAt(LocalDateTime.now())
                    .status("UPLOADED")
                    .build();

            Document saved = documentRepository.save(document);

            return DocumentResponseDTO.builder()
                    .documentId(saved.getId())
                    .fileName(saved.getFileName())
                    .s3Url(saved.getS3Url())
                    .status(saved.getStatus())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }
    }

    @Override
    public byte[] downloadFile(Long documentId) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        return s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(document.getS3Key())
                        .build()
        ).asByteArray();
    }
}
