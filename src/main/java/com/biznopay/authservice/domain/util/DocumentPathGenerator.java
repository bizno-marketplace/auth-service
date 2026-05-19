package com.biznopay.authservice.domain.util;

import com.biznopay.authservice.domain.vo.BiDocument;

import java.util.UUID;

public class DocumentPathGenerator {

    public static BiDocument generateBiDocument(String ownerFolder, String frontExt, String backExt) {
        String base = ownerFolder + "/bi/";
        String frontPath = base + "front-" + UUID.randomUUID() + "." + frontExt;
        String backPath = base + "back-" + UUID.randomUUID() + "." + backExt;
        return BiDocument.of(frontPath, backPath);
    }

//    public static DrivingLicense generateDrivingLicense(String ownerFolder, String frontExt, String backExt) {
//        String base      = ownerFolder + "/driving-license/";
//        String frontPath = base + "front-" + UUID.randomUUID() + "." + frontExt;
//        String backPath  = base + "back-"  + UUID.randomUUID() + "." + backExt;
//        return DrivingLicense.of(frontPath, backPath);
//    }
//
}