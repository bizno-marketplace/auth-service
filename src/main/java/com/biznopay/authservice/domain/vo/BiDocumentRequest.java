package com.biznopay.authservice.domain.vo;

public record BiDocumentRequest(
        byte[] frontPhotoBytes,
        String frontPhotoExt,
        byte[] backPhotoBytes,
        String backPhotoExt
) {
}
