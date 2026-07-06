package com.biznopay.authservice.presentation.service;

import com.biznopay.authservice.grpc.*;
import com.biznopay.authservice.usecase.auth.getUserProfile.GetUserProfile;
import com.biznopay.authservice.usecase.auth.getUserProfile.GetUserProfileInput;
import com.biznopay.authservice.usecase.auth.getUserProfile.GetUserProfileOutput;
import com.biznopay.authservice.usecase.auth.validateToken.ValidateToken;
import com.biznopay.authservice.usecase.auth.validateToken.ValidateTokenInput;
import com.biznopay.authservice.usecase.auth.validateToken.ValidateTokenOutput;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {
    private final ValidateToken validateToken;
    private final GetUserProfile getUserProfile;

    @Override
    public void validateToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
        ValidateTokenInput input = new ValidateTokenInput(request.getToken());
        ValidateTokenOutput output = validateToken.execute(input);
        ValidateTokenResponse response = ValidateTokenResponse.newBuilder().setValid(output.isValid()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserProfile(GetUserProfileRequest request, StreamObserver<GetUserProfileResponse> responseObserver) {
        GetUserProfileInput input =  new GetUserProfileInput(request.getUserId());
        GetUserProfileOutput output = getUserProfile.execute(input);
        GetUserProfileResponse response = buildGetUserProfileResponse(output);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private GetUserProfileResponse buildGetUserProfileResponse(GetUserProfileOutput output){
        return GetUserProfileResponse.newBuilder()
                .setUserId(output.userId())
                .setEmail(output.email())
                .setFirstName(output.firstName())
                .setLastName(output.lastName())
                .setRole(output.role())
                .setStatus(output.status())
                .build();
    }
}
