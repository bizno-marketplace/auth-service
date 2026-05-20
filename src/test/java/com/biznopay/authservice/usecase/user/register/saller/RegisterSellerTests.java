package com.biznopay.authservice.usecase.user.register.saller;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSeller;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSellerInput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class RegisterSellerTests {
    @Mock
    private UserGateway userGateway;

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.SellerTestCases#invalidUseCaseRegisterSellerCases")
    @DisplayName("Should throw exception when seller data already exists or password is weak")
    void shouldThrowExceptionWhenSellerDataAlreadyExistsOrPasswordIsWeak(String testName, RegisterSellerInput input,
                                                                         Optional<User> existingByEmail,
                                                                         Class<? extends Exception> expectedException) {

        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(existingByEmail);
        RegisterSeller registerSeller = new RegisterSeller(userGateway);
        Assertions.assertThatThrownBy(() -> registerSeller.execute(input))
                .isInstanceOf(expectedException);
    }
}
