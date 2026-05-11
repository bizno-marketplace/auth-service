package com.biznopay.authservice.infra.persistence.jpa.repository;

import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.mocks.Mocks;
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

@DataJpaTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SuperAdminJpaRepositoryTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:latest")
    );

    @Autowired
    private SuperAdminJpaRepository superAdminJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setup() {
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("should count zero when no super admin exists")
    public void shouldCountZeroWhenNoSuperAdminExists() {
        long count = superAdminJpaRepository.count();
        Assertions.assertEquals(0, count);
    }

    @Test
    @DisplayName("Should counts 2 when exist 2 super admins")
    public void shouldCountsTwoWhenExistTwoSuperAdmins() {
        UserJpaEntity entity = Mocks.superAdminJpaEntityMock();
        userJpaRepository.save(entity);
        UserJpaEntity entity1 = Mocks.superAdminJpaEntityMock();
        entity1.setEmail("admin1@bizno.co.mz");
        userJpaRepository.save(entity1);
        long count = superAdminJpaRepository.count();
        Assertions.assertEquals(2, count);
    }
}