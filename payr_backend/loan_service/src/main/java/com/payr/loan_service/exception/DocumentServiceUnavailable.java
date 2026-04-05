package com.payr.loan_service.exception;

public class DocumentServiceUnavailable extends RuntimeException {
    public DocumentServiceUnavailable(String message) {
        super(message);
    }
}
