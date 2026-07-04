package com.biznopay.authservice;

import com.biznopay.authservice._config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestcontainersConfiguration.class, TestConfig.class})
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
