package com.biznopay.authservice.presentation.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.multipart.MultipartFile;


@Tag("unit")
public class BiDocumentValidatorTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.BiDocumentValidatorTestCases#validateCases")
    void shouldThrowRequiredFieldExceptionWhenBiDocumentIsNull(
            String testName,
            MultipartFile frontPhoto,
            MultipartFile backPhoto,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) {
        BiDocumentValidator validator = new BiDocumentValidator();
        Assertions.assertThatThrownBy(() -> BiDocumentValidator.validate(frontPhoto, backPhoto))
                .isInstanceOf(expectedException)
                .hasMessage(expectedMessage);
    }
}
