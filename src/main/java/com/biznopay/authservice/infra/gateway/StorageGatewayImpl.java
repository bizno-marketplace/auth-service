package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.gateway.StorageGateway;
import com.biznopay.authservice.domain.vo.StorageFile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageGatewayImpl implements StorageGateway {
    @Override
    public void upload(List<StorageFile> files) {

    }
}
