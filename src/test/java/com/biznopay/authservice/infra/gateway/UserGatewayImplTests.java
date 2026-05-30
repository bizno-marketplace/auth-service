package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.SellerJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.SuperAdminJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.BuyerTestCases.validBuyer;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class UserGatewayImplTests {

    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private SuperAdminJpaRepository superAdminJpaRepository;
    @Mock
    private SellerJpaRepository sellerJpaRepository;

    private UserGatewayImpl setUp() {
        return new UserGatewayImpl(userJpaRepository, superAdminJpaRepository, sellerJpaRepository);
    }

    @Test
    @DisplayName("Should count super admins on countSAs")
    public void shouldCountSuperAdminsOnCountSAs() {
        Mockito.when(superAdminJpaRepository.countBy()).thenReturn(3L);
        UserGatewayImpl userGatewayImpl = setUp();
        long count = userGatewayImpl.countSAs();
        Assertions.assertEquals(3L, count);
        Mockito.verify(superAdminJpaRepository).countBy();
        Mockito.verifyNoMoreInteractions(userJpaRepository);
    }

    @Test
    @DisplayName("Should save user on save")
    public void shouldSaveUserOnSave() {
        User user = validBuyer();
        UserGatewayImpl userGatewayImpl = setUp();
        userGatewayImpl.save(user);
        Mockito.verify(userJpaRepository).save(Mockito.any(UserJpaEntity.class));
        Mockito.verifyNoMoreInteractions(userJpaRepository);
    }

    @Test
    @DisplayName("Should return correct result on find by email")
    public void shouldReturnCorrectResultOnFindByEmail() {
        User user = validBuyer();
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        Mockito.when(userJpaRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(entity));
        UserGatewayImpl userGatewayImpl = setUp();
        Optional<User> result = userGatewayImpl.findByEmail(user.getEmail());

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(entity.getId(), result.get().getId().value());
        Assertions.assertEquals(entity.getFirstName(), result.get().getFirstName());
        Assertions.assertEquals(entity.getLastName(), result.get().getLastName());
        Assertions.assertEquals(entity.getEmail(), result.get().getEmail());
        Assertions.assertEquals(user.getPhone(), result.get().getPhone());
        Assertions.assertEquals(entity.getPassword(), result.get().getPassword());
        Assertions.assertEquals(entity.getStatus(), result.get().getStatus());
        Assertions.assertEquals(entity.getExpiresAt(), result.get().getExpiresAt());
        Assertions.assertEquals(entity.getCreatedAt(), result.get().getCreatedAt());
        Assertions.assertEquals(entity.getUpdatedAt(), result.get().getUpdatedAt());

        Mockito.verify(userJpaRepository).findByEmail(user.getEmail());
        Mockito.verifyNoMoreInteractions(userJpaRepository);
    }

    @Test
    @DisplayName("Should return optional empty when user does not exists on find by id")
    public void shouldReturnOptionalEmptyWhenUserDoesNotExistsOnFindById() {
        UUID useId = UUID.randomUUID();
        Mockito.when(userJpaRepository.findById(useId)).thenReturn(Optional.empty());
        UserGatewayImpl userGatewayImpl = setUp();
        Optional<User> result = userGatewayImpl.findById(useId);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(userJpaRepository).findById(useId);
        Mockito.verifyNoMoreInteractions(userJpaRepository);
    }

    @Test
    @DisplayName("Should return user when exists on find by id")
    public void shouldReturnUserWhenExistsOnFindById() {
        User user = validBuyer();
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        Mockito.when(userJpaRepository.findById(user.getId().value())).thenReturn(Optional.of(entity));
        UserGatewayImpl userGatewayImpl = setUp();
        Optional<User> result = userGatewayImpl.findById(user.getId().value());

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(entity.getId(), result.get().getId().value());
        Assertions.assertEquals(entity.getFirstName(), result.get().getFirstName());
        Assertions.assertEquals(entity.getLastName(), result.get().getLastName());
        Assertions.assertEquals(entity.getEmail(), result.get().getEmail());
        Assertions.assertEquals(user.getPhone(), result.get().getPhone());
        Assertions.assertEquals(entity.getPassword(), result.get().getPassword());
        Assertions.assertEquals(entity.getStatus(), result.get().getStatus());
        Assertions.assertEquals(entity.getExpiresAt(), result.get().getExpiresAt());
        Assertions.assertEquals(entity.getCreatedAt(), result.get().getCreatedAt());
        Assertions.assertEquals(entity.getUpdatedAt(), result.get().getUpdatedAt());

        Mockito.verify(userJpaRepository).findById(user.getId().value());
        Mockito.verifyNoMoreInteractions(userJpaRepository);
    }
}
