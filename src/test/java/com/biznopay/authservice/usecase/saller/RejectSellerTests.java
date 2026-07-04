package com.biznopay.authservice.usecase.saller;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.seller.Seller;
import com.biznopay.authservice.domain.entity.user.seller.SellerRejection;
import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.InvalidSellerAccountStatus;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.gateway.SellerRejectionGateway;
import com.biznopay.authservice.domain.gateway.TransactionGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.policy.RejectSellerPolicy;
import com.biznopay.authservice.infra.gateway.TransactionGatewayImpl;
import com.biznopay.authservice.usecase.seller.rejectSeller.RejectSeller;
import com.biznopay.authservice.usecase.seller.rejectSeller.RejectSellerInput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.SellerTestCases.VALID_SELLER;
import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN_NEW;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class RejectSellerTests {
    TransactionGateway transactionGateway = new TransactionGatewayImpl();
    @Mock
    AuthenticationGateway authenticationGateway;
    @Mock
    RejectSellerPolicy rejectSellerPolicy;
    @Mock
    UserGateway userGateway;
    @Mock
    SellerRejectionGateway sellerRejectionGateway;

    public RejectSeller setUp(){
        return new RejectSeller(transactionGateway,authenticationGateway,rejectSellerPolicy,userGateway,sellerRejectionGateway);
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when seller id is invalid")
    public void shouldThrowInvalidFieldExceptionWhenSellerIdIsInvalid(){
        RejectSellerInput input =  new RejectSellerInput("any_seller_id","any_reason");

        User sa =  VALID_SUPER_ADMIN_NEW;

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(sa);
        Mockito.doNothing().when(rejectSellerPolicy).enforce(sa,"REJECT_SELLER-001");

        RejectSeller rejectSeller =  setUp();
        Assertions.assertThatThrownBy(() -> rejectSeller.execute(input))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Invalid User Id on REJECT_SELLER");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if reason is null or empty")
    public void shouldThrowRequiredFieldExceptionIfReasonIsNullOrEmpty(String reason){
        RejectSellerInput input =  new RejectSellerInput(UUID.randomUUID().toString(),reason);

        User sa =  VALID_SUPER_ADMIN_NEW;

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(sa);
        Mockito.doNothing().when(rejectSellerPolicy).enforce(sa,"REJECT_SELLER-001");

        RejectSeller rejectSeller =  setUp();
        Assertions.assertThatThrownBy(() -> rejectSeller.execute(input))
                .isInstanceOf(RequiredFieldException.class)
                .hasMessage("Reason for Rejection is required");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when seller does not exist")
    public void shouldThrowResourceNotFoundExceptionWhenSellerDoesNotExist(){
        UUID sellerId = UUID.randomUUID();
        String rawSellerId = sellerId.toString();
        RejectSellerInput input =  new RejectSellerInput(rawSellerId,"any_reason");

        User sa =  VALID_SUPER_ADMIN_NEW;
        Mockito.when(authenticationGateway.loggedUser()).thenReturn(sa);
        Mockito.doNothing().when(rejectSellerPolicy).enforce(sa,"REJECT_SELLER-001");
        Mockito.when(userGateway.findSellerById(sellerId)).thenReturn(Optional.empty());

        RejectSeller rejectSeller =  setUp();
        Assertions.assertThatThrownBy(() -> rejectSeller.execute(input))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Seller not found");

    }


    @Test
    @DisplayName("Should throw InvalidSellerAccountStatus when account is not in AWAITING_APPROVAL")
    public void shouldThrowInvalidSellerAccountStatusWhenAccountIsNotInAwaitingApproval(){
        Seller seller =  VALID_SELLER;

        UUID sellerId = seller.getId().value();
        String rawSellerId = sellerId.toString();
        RejectSellerInput input =  new RejectSellerInput(rawSellerId,"any_reason");

        User sa =  VALID_SUPER_ADMIN_NEW;

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(sa);
        Mockito.doNothing().when(rejectSellerPolicy).enforce(sa,"REJECT_SELLER-001");
        Mockito.when(userGateway.findSellerById(sellerId)).thenReturn(Optional.of(seller));

        RejectSeller rejectSeller =  setUp();
        Assertions.assertThatThrownBy(() -> rejectSeller.execute(input))
                .isInstanceOf(InvalidSellerAccountStatus.class)
                .hasMessage("Can only perform this action to Sellers with status AWAITING_APPROVAL");
    }

    @Test
    @DisplayName("Should save seller rejection and block seller if rejection count reaches 3 attempts")
    public void shouldSaveSellerRejectionAndBlockSellerIfRejectionCountReaches3Attempts(){
        Seller seller =  VALID_SELLER;
        seller.setToAwaitingForApproval();

        UUID sellerId = seller.getId().value();
        String rawSellerId = sellerId.toString();
        String reasonForRejection = "any_reason";

        RejectSellerInput input =  new RejectSellerInput(rawSellerId,reasonForRejection);
        SellerRejection sellerRejection =  SellerRejection.of(sellerId,"any_reason");
        sellerRejection.increaseNumberOfAttempts(reasonForRejection);
        sellerRejection.increaseNumberOfAttempts(reasonForRejection);
        sellerRejection.increaseNumberOfAttempts(reasonForRejection);

        User sa =  VALID_SUPER_ADMIN_NEW;

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(sa);
        Mockito.doNothing().when(rejectSellerPolicy).enforce(sa,"REJECT_SELLER-001");
        Mockito.when(userGateway.findSellerById(sellerId)).thenReturn(Optional.of(seller));
        Mockito.when(sellerRejectionGateway.findByUserId(sellerId)).thenReturn(Optional.of(sellerRejection));

        RejectSeller rejectSeller =  setUp();
        rejectSeller.execute(input);

        Mockito.verify(authenticationGateway,Mockito.times(1)).loggedUser();
        Mockito.verify(rejectSellerPolicy,Mockito.times(1)).enforce(sa,"REJECT_SELLER-001");
        Mockito.verify(userGateway,Mockito.times(1)).findSellerById(sellerId);
        Mockito.verify(sellerRejectionGateway,Mockito.times(1)).findByUserId(sellerId);
        Mockito.verify(sellerRejectionGateway,Mockito.times(1)).save(sellerRejection);
        Mockito.verify(userGateway,Mockito.times(1)).save(seller);
    }

    @Test
    @DisplayName("Should save seller rejection and reject seller if rejection count does not reach 3 attempts")
    public void shouldSaveSellerRejectionAndRejectSellerIfRejectionCountDoesNotReach3Attempts(){
        Seller seller =  VALID_SELLER;
        seller.setToAwaitingForApproval();

        UUID sellerId = seller.getId().value();
        String rawSellerId = sellerId.toString();
        String reasonForRejection = "any_reason";

        RejectSellerInput input =  new RejectSellerInput(rawSellerId,reasonForRejection);
        SellerRejection sellerRejection =  SellerRejection.of(sellerId,"any_reason");

        User sa =  VALID_SUPER_ADMIN_NEW;

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(sa);
        Mockito.doNothing().when(rejectSellerPolicy).enforce(sa,"REJECT_SELLER-001");
        Mockito.when(userGateway.findSellerById(sellerId)).thenReturn(Optional.of(seller));
        Mockito.when(sellerRejectionGateway.findByUserId(sellerId)).thenReturn(Optional.of(sellerRejection));

        RejectSeller rejectSeller =  setUp();
        rejectSeller.execute(input);

        Mockito.verify(authenticationGateway,Mockito.times(1)).loggedUser();
        Mockito.verify(rejectSellerPolicy,Mockito.times(1)).enforce(sa,"REJECT_SELLER-001");
        Mockito.verify(userGateway,Mockito.times(1)).findSellerById(sellerId);
        Mockito.verify(sellerRejectionGateway,Mockito.times(1)).findByUserId(sellerId);
        Mockito.verify(sellerRejectionGateway,Mockito.times(1)).save(sellerRejection);
        Mockito.verify(userGateway,Mockito.times(1)).save(seller);
    }

}
