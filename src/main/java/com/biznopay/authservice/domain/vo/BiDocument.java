package com.biznopay.authservice.domain.vo;

public class BiDocument {

    private final String frontPath;
    private final String backPath;

    private BiDocument(String frontPath, String backPath) {
        this.frontPath = frontPath;
        this.backPath = backPath;
    }

    public static BiDocument of(String frontPath, String backPath) {
        return new BiDocument(frontPath, backPath);
    }

    public String getFrontPath() {
        return frontPath;
    }

    public String getBackPath() {
        return backPath;
    }
}