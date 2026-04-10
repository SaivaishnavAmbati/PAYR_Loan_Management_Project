package com.payr.document_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {

    private Long documentId;
    private String fileName;
    private String s3Url;
    private String status;
}

