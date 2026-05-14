package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.gateway.EncoderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncoderGatewayImpl implements EncoderGateway {
    private final BCryptPasswordEncoder encoder;

    @Override
    public String encode(String rawValue) {
        return encoder.encode(rawValue);
    }
}
