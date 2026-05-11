package com.biznopay.authservice.mocks;

import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.BuyerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSAInput;

import java.time.LocalDateTime;
import java.util.UUID;

public class Mocks {
    public static RegisterSARequest registerSARequestMock() {
        return new RegisterSARequest("John", "Smith", "johnsmith@bizno.co.mz", "Password@123");
    }

    public static RegisterSAInput registerSAInputMock() {
        return new RegisterSAInput("John", "Smith", "johnsmith@bizno.co.mz", "Password@123");
    }

    public static RegisterSARequest registerSARequestEmptyFieldMock(String fieldName){
        return switch (fieldName){
            case "firstname" -> new RegisterSARequest("", "Smith", "johnsmith@bizno.co.mz", "Password@123");
            case "lastname" -> new RegisterSARequest("John", "", "johnsmith@bizno.co.mz", "Password@123");
            case "email" -> new RegisterSARequest("John", "Smith", "", "Password@123");
            case "password" -> new RegisterSARequest("John", "Smith", "johnsmith@bizno.co.mz", "");
            default -> throw new IllegalArgumentException("Invalid field name");
        };
    }

    public static RegisterSARequest registerSARequestInvalidFieldMock(String fieldName) {
        return switch (fieldName){
            case "firstname" -> new RegisterSARequest("Jo", "Smith", "johnsmith@bizno.co.mz", "Password@123");
            case "lastname" -> new RegisterSARequest("John", "Sm", "johnsmith@bizno.co.mz", "Password@123");
            case "email" -> new RegisterSARequest("John", "Smith", "johnsmith@gmail.com", "Password@123");
            case "password" -> new RegisterSARequest("John", "Smith", "johnsmith@bizno.co.mz", "Pass");
            default -> throw new IllegalArgumentException("Invalid field name");
        };
    }

    public static User superAdminMock(){
        return SuperAdmin.register("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
    }

    public static User superAdminMockFromRegisterSARequest(RegisterSARequest request){
        return SuperAdmin.register(request.firstName(), request.lastName(), request.email(), request.password());
    }

    public static  UserJpaEntity superAdminJpaEntityMock(){
        return new SuperAdminJpaEntity(UUID.randomUUID(), "any_first_name", "any_last_name",
                "admin@bizno.co.mz", "", "Password@123", UserStatus.PENDING, LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), LocalDateTime.now());
    }

    public static  UserJpaEntity supperAdminJpaEntityMockFromSuperAdmin(){
        User user =  superAdminMockFromRegisterSARequest(registerSARequestMock());
        return UserMapper.toUserJpaEntity(user);
    }

    public static User buyerMock(){
        return Buyer.register("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
    }

    public static User buyerMockFromRegisterSARequest(RegisterSARequest request){
        return Buyer.register(request.firstName(), request.lastName(), request.email(), request.password());
    }

    public static  UserJpaEntity buyerJpaEntityMock(){
        return new BuyerJpaEntity(UUID.randomUUID(), "any_first_name", "any_last_name",
                "admin@bizno.co.mz", "", "Password@123", UserStatus.PENDING, LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), LocalDateTime.now());
    }

    public static  UserJpaEntity buyerJpaEntityMockFromBuyer(){
        User user =  buyerMockFromRegisterSARequest(registerSARequestMock());
        return UserMapper.toUserJpaEntity(user);
    }
}
