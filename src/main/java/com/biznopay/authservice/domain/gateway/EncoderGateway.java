package com.biznopay.authservice.domain.gateway;

public interface EncoderGateway {
    String encode(String rawValue);

    boolean matches(String rawValue, String encodedValue);
}
