package com.biznopay.authservice.presentation.validator;

import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class BiDocumentValidator {

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png"
    );

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    public static void validate(MultipartFile front, MultipartFile back) {
        validateFile(front, "BI front photo");
        validateFile(back, "BI back photo");
    }

    private static void validateFile(MultipartFile file, String fieldName) {
        if (file == null || file.isEmpty()) {
            throw new RequiredFieldException(fieldName, BiDocumentValidator.class.getName(), "BIDOCUMENTVALIDATOR-001");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidFieldException(fieldName, BiDocumentValidator.class.getName(), "BIDOCUMENTVALIDATOR-002");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new InvalidFieldException(fieldName, BiDocumentValidator.class.getName(), "BIDOCUMENTVALIDATOR-003");
        }
    }
}