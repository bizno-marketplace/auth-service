package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class AddressTestCases {
    public static final Double VALID_LATITUDE = -25.9692;
    public static final Double VALID_LONGITUDE = 32.5732;
    public static final String VALID_STREET = "Av. 24 de Julho";
    public static final String VALID_NEIGHBOURHOOD = "Sommerschield";
    public static final String VALID_CITY = "Maputo";
    public static final String VALID_PROVINCE = "Maputo";
    public static final String VALID_COUNTRY = "Mozambique";

    public static Stream<Arguments> registerAddressDomainTestCases(){
        return Stream.of(
                Arguments.of("Latitude is null", null, VALID_LONGITUDE,VALID_STREET, VALID_NEIGHBOURHOOD, VALID_CITY, VALID_PROVINCE, VALID_COUNTRY, RequiredFieldException.class, "Latitude is required"),
                Arguments.of("Latitude is less than -90", -91.0, VALID_LONGITUDE,VALID_STREET, VALID_NEIGHBOURHOOD, VALID_CITY, VALID_PROVINCE, VALID_COUNTRY, InvalidFieldException.class, "Invalid Latitude on Address"),
                Arguments.of("Latitude is higher than 90", 91.0, VALID_LONGITUDE,VALID_STREET, VALID_NEIGHBOURHOOD, VALID_CITY, VALID_PROVINCE, VALID_COUNTRY, InvalidFieldException.class, "Invalid Latitude on Address"),
                Arguments.of("Longitude is null", VALID_LATITUDE, null,VALID_STREET, VALID_NEIGHBOURHOOD, VALID_CITY, VALID_PROVINCE, VALID_COUNTRY, RequiredFieldException.class, "Longitude is required"),
                Arguments.of("Longitude is less than -180",VALID_LATITUDE, -181.0,VALID_STREET, VALID_NEIGHBOURHOOD, VALID_CITY, VALID_PROVINCE, VALID_COUNTRY, InvalidFieldException.class, "Invalid Longitude on Address"),
                Arguments.of("Longitude is higher than 180",VALID_LATITUDE, 181.0,VALID_STREET, VALID_NEIGHBOURHOOD, VALID_CITY, VALID_PROVINCE, VALID_COUNTRY, InvalidFieldException.class, "Invalid Longitude on Address"),
                Arguments.of("Success",VALID_LATITUDE, VALID_LONGITUDE,VALID_STREET, VALID_NEIGHBOURHOOD, VALID_CITY, VALID_PROVINCE, VALID_COUNTRY, null, null)
        );
    }

}
