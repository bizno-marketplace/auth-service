package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.exception.FileSizeExceedLimitException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.exception.UnsupportedFileTypeException;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class BiDocumentValidatorTestCases {
    public static final MultipartFile VALID_FRONT_BI_DOCUMENT = new MultipartFile() {
        @Override
        public String getName() {
            return "bi_frente.png";
        }

        @Override
        public @Nullable String getOriginalFilename() {
            return "bi_frente.png";
        }

        @Override
        public @Nullable String getContentType() {
            return "image/png";
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 5 * 1024 * 1024;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return "bi_frente.png".getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };
    public static final MultipartFile VALID_BACK_BI_DOCUMENT = new MultipartFile() {
        @Override
        public String getName() {
            return "bi_verso.png";
        }

        @Override
        public @Nullable String getOriginalFilename() {
            return "bi_verso.png";
        }

        @Override
        public @Nullable String getContentType() {
            return "image/png";
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 5 * 1024 * 1024;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return "bi_verso.png".getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };

    public static final MultipartFile EMPTY_FRONT_BI_DOCUMENT = new MultipartFile() {
        @Override
        public String getName() {
            return "bi_frente.png";
        }

        @Override
        public @Nullable String getOriginalFilename() {
            return "bi_frente.png";
        }

        @Override
        public @Nullable String getContentType() {
            return "image/png";
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public long getSize() {
            return 5 * 1024 * 1024;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return "bi_frente.png".getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };
    public static final MultipartFile EMPY_BACK_BI_DOCUMENT = new MultipartFile() {
        @Override
        public String getName() {
            return "bi_verso.png";
        }

        @Override
        public @Nullable String getOriginalFilename() {
            return "bi_verso.png";
        }

        @Override
        public @Nullable String getContentType() {
            return "image/png";
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public long getSize() {
            return 5 * 1024 * 1024;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return "bi_verso.png".getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };

    public static final MultipartFile INVALID_FRONT_BI_DOCUMENT = new MultipartFile() {
        @Override
        public String getName() {
            return "bi_frente.txt";
        }

        @Override
        public @Nullable String getOriginalFilename() {
            return "bi_frente.txt";
        }

        @Override
        public @Nullable String getContentType() {
            return "text/txt";
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 5 * 1024 * 1024;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return "bi_frente.txt".getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };
    public static final MultipartFile INVALID_BACK_BI_DOCUMENT = new MultipartFile() {
        @Override
        public String getName() {
            return "bi_back.txt";
        }

        @Override
        public @Nullable String getOriginalFilename() {
            return "bi_back.txt";
        }

        @Override
        public @Nullable String getContentType() {
            return "text/txt";
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 5 * 1024 * 1024;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return "bi_back.txt".getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };

    public static final MultipartFile MAX_SIZE_FRONT_BI_DOCUMENT = new MultipartFile() {
        @Override
        public String getName() {
            return "bi_frente.png";
        }

        @Override
        public @Nullable String getOriginalFilename() {
            return "bi_frente.png";
        }

        @Override
        public @Nullable String getContentType() {
            return "image/png";
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 6 * 1024 * 1024;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return "bi_frente.png".getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };
    public static final MultipartFile MAX_SIZE_BACk_BI_DOCUMENT = new MultipartFile() {
        @Override
        public String getName() {
            return "bi_verso.png";
        }

        @Override
        public @Nullable String getOriginalFilename() {
            return "bi_verso.png";
        }

        @Override
        public @Nullable String getContentType() {
            return "image/png";
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 6 * 1024 * 1024;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return "bi_verso.png".getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };


    public static Stream<Arguments> validateCases() {
        return Stream.of(
                Arguments.of("Front bi photo is null", null, VALID_BACK_BI_DOCUMENT, RequiredFieldException.class, "BI front photo is required"),
                Arguments.of("Front bi photo is empty", EMPTY_FRONT_BI_DOCUMENT, VALID_BACK_BI_DOCUMENT, RequiredFieldException.class, "BI front photo is required"),
                Arguments.of("Front bi photo is invalid", INVALID_FRONT_BI_DOCUMENT, VALID_BACK_BI_DOCUMENT, UnsupportedFileTypeException.class, "File type not supported form field BI front photo. Only [image/jpeg, image/png] are allowed"),
                Arguments.of("Front bi photo is too large", MAX_SIZE_FRONT_BI_DOCUMENT, VALID_BACK_BI_DOCUMENT, FileSizeExceedLimitException.class, "BI front photo exceeds the maximum size of 5MB"),
                Arguments.of("Back bi photo is null", VALID_FRONT_BI_DOCUMENT, null, RequiredFieldException.class, "BI back photo is required"),
                Arguments.of("Back bi photo is empty", VALID_FRONT_BI_DOCUMENT, EMPY_BACK_BI_DOCUMENT, RequiredFieldException.class, "BI back photo is required"),
                Arguments.of("Back bi photo is invalid", VALID_FRONT_BI_DOCUMENT, INVALID_BACK_BI_DOCUMENT, UnsupportedFileTypeException.class, "File type not supported form field BI back photo. Only [image/jpeg, image/png] are allowed"),
                Arguments.of("Back bi photo is too large", VALID_FRONT_BI_DOCUMENT, MAX_SIZE_BACk_BI_DOCUMENT, FileSizeExceedLimitException.class, "BI back photo exceeds the maximum size of 5MB")
        );
    }
}
