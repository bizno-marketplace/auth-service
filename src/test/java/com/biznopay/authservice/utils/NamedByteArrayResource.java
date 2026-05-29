package com.biznopay.authservice.utils;

import org.springframework.core.io.ByteArrayResource;

public class NamedByteArrayResource extends ByteArrayResource {
    private final String filename;

    public NamedByteArrayResource(byte[] bytes, String filename) {
        super(bytes);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}