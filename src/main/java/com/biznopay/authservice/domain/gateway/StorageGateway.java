package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.vo.StorageFile;

import java.util.List;

public interface StorageGateway {
    void upload(List<StorageFile> files);
}
