package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.gateway.EncoderGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class EncoderGatewayTests {
    @Mock
    private BCryptPasswordEncoder encoder;

    @Test
    @DisplayName("Should encode the password")
    public void shouldEncodePassword() {
        String rawPassword = "any_raw_password";
        String encodePassword =  "any_encoded_password";
        Mockito.when(encoder.encode(rawPassword)).thenReturn(encodePassword);
        EncoderGateway encoderGateway =  new EncoderGatewayImpl(encoder);
        String result = encoderGateway.encode(rawPassword);
        Assertions.assertNotNull(rawPassword);
        Assertions.assertEquals(encodePassword,result);
        Mockito.verify(encoder,Mockito.times(1)).encode(rawPassword);
    }
}
