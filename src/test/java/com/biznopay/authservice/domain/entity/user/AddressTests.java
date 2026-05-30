package com.biznopay.authservice.domain.entity.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("unit")
public class AddressTests {

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.AddressTestCases#buildAddressDomainTestCases")
    public void registerAddressDomainTestCases(
            String testName,
            Double latitude,
            Double longitude,
            String street,
            String neighbourhood,
            String city,
            String province,
            String country,
            Class<? extends Exception> expectedException,
            String expectedExceptionMessage
    ) {
        if (testName.equals("Success")){
            Address address = Address.of(latitude,longitude,street,neighbourhood,city,province,country);

            Assertions.assertThat(address).isNotNull();
            Assertions.assertThat(address.getId()).isNull();
            Assertions.assertThat(address.getLatitude()).isEqualTo(latitude);
            Assertions.assertThat(address.getLongitude()).isEqualTo(longitude);
            Assertions.assertThat(address.getStreet()).isEqualTo(street);
            Assertions.assertThat(address.getNeighbourhood()).isEqualTo(neighbourhood);
            Assertions.assertThat(address.getCity()).isEqualTo(city);
            Assertions.assertThat(address.getProvince()).isEqualTo(province);
            Assertions.assertThat(address.getCountry()).isEqualTo(country);
        }else {
            Assertions.assertThatThrownBy(() -> Address.of(latitude,longitude,street,neighbourhood,city,province,country))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedExceptionMessage);
        }

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.AddressTestCases#reconstructAddressDomainTestCases")
    public void reconstructAddressDomainTestCases(
            String testName,
            Long id,
            Double latitude,
            Double longitude,
            String street,
            String neighbourhood,
            String city,
            String province,
            String country,
            Class<? extends Exception> expectedException,
            String expectedExceptionMessage
    ) {
        if (testName.equals("Success")){
            Address address = Address.reconstruct(id,latitude,longitude,street,neighbourhood,city,province,country);

            Assertions.assertThat(address).isNotNull();
            Assertions.assertThat(address.getId()).isNotNull();
            Assertions.assertThat(address.getLatitude()).isEqualTo(latitude);
            Assertions.assertThat(address.getLongitude()).isEqualTo(longitude);
            Assertions.assertThat(address.getStreet()).isEqualTo(street);
            Assertions.assertThat(address.getNeighbourhood()).isEqualTo(neighbourhood);
            Assertions.assertThat(address.getCity()).isEqualTo(city);
            Assertions.assertThat(address.getProvince()).isEqualTo(province);
            Assertions.assertThat(address.getCountry()).isEqualTo(country);
        }else {
            Assertions.assertThatThrownBy(() -> Address.reconstruct(id,latitude,longitude,street,neighbourhood,city,province,country))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedExceptionMessage);
        }

    }
}
