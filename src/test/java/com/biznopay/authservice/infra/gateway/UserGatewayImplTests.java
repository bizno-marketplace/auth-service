package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserGatewayImplTests {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("Should count super admins on countSAs")
    public void shouldCountSuperAdminsOnCountSAs() {
        Mockito.when(userJpaRepository.countSAs()).thenReturn(3L);
        UserGatewayImpl userGatewayImpl = new UserGatewayImpl(userJpaRepository);
        long count = userGatewayImpl.countSAs();
        Assertions.assertEquals(3L, count);
        Mockito.verify(userJpaRepository).countSAs();
        Mockito.verifyNoMoreInteractions(userJpaRepository);
    }
}
