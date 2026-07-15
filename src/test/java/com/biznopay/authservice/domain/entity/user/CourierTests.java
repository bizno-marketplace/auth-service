package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.enums.VehicleTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.UUID;

@Tag("unit")
public class CourierTests {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.CourierTestCases#registerDomainCases")
    public void registerDomainCases(String testName, String firstName, String lastName, String email,
                                    String phone, String password, VehicleTypeEnum vehicleType, String licenseNumber, String zone,
                                    Class<? extends Exception> expectedException, String expectedMessage) {
        if (testName.equals("Success")) {
            Courier courier = Courier.register(firstName, lastName, email, phone, password, vehicleType, licenseNumber, zone);
            Assertions.assertNotNull(courier);
            Assertions.assertNotNull(courier.getId());
            Assertions.assertEquals(firstName, courier.getFirstName());
            Assertions.assertEquals(lastName, courier.getLastName());
            Assertions.assertEquals(email, courier.getEmail());
            Assertions.assertEquals(phone, courier.getPhone());
            Assertions.assertEquals(password, courier.getPassword());
            Assertions.assertEquals(UserStatus.PENDING, courier.getStatus());
            Assertions.assertEquals(vehicleType, courier.getVehicleType());
            Assertions.assertEquals(licenseNumber, courier.getLicenseNumber());
            Assertions.assertEquals(zone, courier.getZone());
            Assertions.assertNull(courier.getExpiresAt());
            Assertions.assertNotNull(courier.getCreatedAt());
            Assertions.assertNotNull(courier.getUpdatedAt());
        } else {
            org.assertj.core.api.Assertions.assertThatThrownBy(
                            () -> Courier.register(firstName, lastName, email, phone, password, vehicleType, licenseNumber, zone))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedMessage);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.CourierTestCases#reconstructDomainCases")
    public void reconstructDomainCases(String testName, UUID userId, String firstName, String lastName, String email,
                                       String phone, String password, VehicleTypeEnum vehicleType, String licenseNumber, String zone,
                                       UserStatus status, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                                       Class<? extends Exception> expectedException, String expectedMessage) {
        if (testName.equals("Success")) {
            Courier courier = Courier.reconstruct(userId, firstName, lastName, email, phone, password, vehicleType,
                    licenseNumber, zone, status, expiresAt, createdAt, updatedAt);
            Assertions.assertNotNull(courier);
            Assertions.assertNotNull(courier.getId());
            Assertions.assertEquals(firstName, courier.getFirstName());
            Assertions.assertEquals(lastName, courier.getLastName());
            Assertions.assertEquals(email, courier.getEmail());
            Assertions.assertEquals(phone, courier.getPhone());
            Assertions.assertEquals(password, courier.getPassword());
            Assertions.assertEquals(UserStatus.PENDING, courier.getStatus());
            Assertions.assertEquals(vehicleType, courier.getVehicleType());
            Assertions.assertEquals(licenseNumber, courier.getLicenseNumber());
            Assertions.assertEquals(zone, courier.getZone());
            Assertions.assertEquals(expiresAt, courier.getExpiresAt());
            Assertions.assertNotNull(courier.getCreatedAt());
            Assertions.assertNotNull(courier.getUpdatedAt());
        } else {
            org.assertj.core.api.Assertions.assertThatThrownBy(
                            () -> Courier.reconstruct(userId, firstName, lastName, email, phone, password, vehicleType,
                                    licenseNumber, zone, status, expiresAt, createdAt, updatedAt))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedMessage);
        }
    }
}
