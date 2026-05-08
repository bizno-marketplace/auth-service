package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.SuperAdminJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSAInput;
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
    @Mock
    private SuperAdminJpaRepository superAdminJpaRepository;

    @Test
    @DisplayName("Should count super admins on countSAs")
    public void shouldCountSuperAdminsOnCountSAs() {
        Mockito.when(superAdminJpaRepository.countBy()).thenReturn(3L);
        UserGatewayImpl userGatewayImpl = new UserGatewayImpl(userJpaRepository,superAdminJpaRepository);
        long count = userGatewayImpl.countSAs();
        Assertions.assertEquals(3L, count);
        Mockito.verify(superAdminJpaRepository).countBy();
        Mockito.verifyNoMoreInteractions(userJpaRepository);
    }

    @Test
    @DisplayName("Should save user on save")
    public  void shouldSaveUserOnSave(){
        RegisterSAInput input = new RegisterSAInput("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        User user = SuperAdmin.register(input.firstName(), input.lastName(), input.email(), input.password());
        UserGatewayImpl userGatewayImpl = new UserGatewayImpl(userJpaRepository,superAdminJpaRepository);
        userGatewayImpl.save(user);
        Mockito.verify(userJpaRepository).save(Mockito.any(UserJpaEntity.class));
        Mockito.verifyNoMoreInteractions(userJpaRepository);
    }
}
