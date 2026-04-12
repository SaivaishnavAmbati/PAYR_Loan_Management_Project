package com.payr.document_service.service.impl;

import com.payr.document_service.dto.DocumentResponseDTO;
import com.payr.document_service.exception.ResourceNotFoundException;
import com.payr.document_service.model.Document;
import com.payr.document_service.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(documentService, "bucketName", BUCKET_NAME);
    }

    @Test
    void uploadFile_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getBytes()).thenReturn("file content".getBytes());

        Document savedDoc = Document.builder()
                .id(1L)
                .fileName("test.pdf")
                .s3Url("http://s3.url/test.pdf")
                .status("UPLOADED")
                .build();

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(documentRepository.save(any(Document.class))).thenReturn(savedDoc);

        DocumentResponseDTO result = documentService.uploadFile(1L, file);

        assertNotNull(result);
        assertEquals(1L, result.getDocumentId());
        assertEquals("test.pdf", result.getFileName());
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void uploadFile_Failure_ThrowsRuntimeException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenThrow(new IOException("Read error"));

        assertThrows(RuntimeException.class, () -> documentService.uploadFile(1L, file));
    }

    @Test
    void downloadFile_Success() {
        Document document = Document.builder()
                .id(1L)
                .s3Key("1/uuid_test.pdf")
                .build();

        byte[] content = "file content".getBytes();
        @SuppressWarnings("unchecked")
        ResponseBytes<GetObjectResponse> responseBytes = mock(ResponseBytes.class);
        when(responseBytes.asByteArray()).thenReturn(content);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        byte[] result = documentService.downloadFile(1L);

        assertArrayEquals(content, result);
        verify(documentRepository).findById(1L);
        verify(s3Client).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    void downloadFile_NotFound_ThrowsException() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> documentService.downloadFile(1L));
    }
}
