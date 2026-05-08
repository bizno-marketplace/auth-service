package com.biznopay.authservice.infra.persistence.repository;

import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.SuperAdminJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSAInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserJpaRepositoryTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:latest")
    );

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setup() {
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return optional empty when user  not exist on find by email")
    public void shouldReturnOptionalEmptyWhenUserNotExistOnFindByEmail() {
        Optional<UserJpaEntity> user = userJpaRepository.findByEmail("any_email");
        Assertions.assertTrue(user.isEmpty());
    }

    @Test
    @DisplayName("Should return user when exist on find by email")
    public  void shouldReturnUserWhenExistOnFindByEmail() {
        RegisterSAInput input = new RegisterSAInput("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        User user = SuperAdmin.register(input.firstName(), input.lastName(), input.email(), input.password());
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);

        Optional<UserJpaEntity> result = userJpaRepository.findByEmail("admin@bizno.co.mz");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(entity.getId(), result.get().getId());
        Assertions.assertEquals(entity.getFirstName(), result.get().getFirstName());
        Assertions.assertEquals(entity.getLastName(), result.get().getLastName());
        Assertions.assertEquals(entity.getEmail(), result.get().getEmail());
        Assertions.assertEquals("", result.get().getPhone());
        Assertions.assertEquals(entity.getPassword(), result.get().getPassword());
        Assertions.assertEquals(entity.getStatus(), result.get().getStatus());
        Assertions.assertEquals(entity.getExpiresAt(), result.get().getExpiresAt());
        Assertions.assertEquals(entity.getCreatedAt(), result.get().getCreatedAt());
        Assertions.assertEquals(entity.getUpdatedAt(), result.get().getUpdatedAt());
    }
}
