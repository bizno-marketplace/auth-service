package com.biznopay.authservice.usecase.saller;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.InvalidSellerAccountStatus;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.policy.ApproveSellerPolicy;
import com.biznopay.authservice.usecase.seller.approveSeller.ApproveSeller;
import com.biznopay.authservice.usecase.seller.approveSeller.ApproveSellerInput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.SellerTestCases.VALID_SELLER;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ApproveSellerTest {
    @Mock
    private ApproveSellerPolicy policy;
    @Mock
    private AuthenticationGateway authenticationGateway;
    @Mock
    UserGateway userGateway;

    @InjectMocks
    private ApproveSeller usecase;

    @Test
    @DisplayName("Should throw InvalidFieldException when seller id is invalid")
    public void shouldThrowInvalidFieldExceptionWhenSellerIdIsInvalid() {
        ApproveSellerInput input = new ApproveSellerInput("any_saller_id");
        Assertions.assertThatThrownBy(() -> usecase.execute(input)).isInstanceOf(InvalidFieldException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when authenticated user not found in database")
    public void shouldThrowResourceNotFoundExceptionWhenAuthenticatedUserNotFoundInDatabase() {
        ApproveSellerInput input = new ApproveSellerInput(UUID.randomUUID().toString());
        Mockito.when(authenticationGateway.loggedUser()).thenReturn(VALID_SELLER);
        Mockito.doNothing().when(policy).enforce(VALID_SELLER, "APPROVE_SELLER-001");

        Assertions.assertThatThrownBy(() -> usecase.execute(input))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Seller not found");
    }

    @Test
    @DisplayName("Should throw InvalidSellerAccountStatus when seller account status is not AWAITING_APPROVAL")
    public void shouldThrowInvalidSellerAccountStatusWhenSellerAccountStatusIsNotAwaitingApproval() {
        ApproveSellerInput input = new ApproveSellerInput(UUID.randomUUID().toString());

        User seller = VALID_SELLER;
        seller.activate();

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);
        Mockito.doNothing().when(policy).enforce(seller, "APPROVE_SELLER-001");
        Mockito.when(userGateway.findSellerById(Mockito.any())).thenReturn(Optional.of(seller));

        Assertions.assertThatThrownBy(() -> usecase.execute(input))
                .isInstanceOf(InvalidSellerAccountStatus.class)
                .hasMessage("Can only perform this action to Sellers with status AWAITING_APPROVAL");
    }

    @Test
    @DisplayName("Should approve seller successfully")
    public void shouldApproveSellerSuccessfully() {
        ApproveSellerInput input = new ApproveSellerInput(UUID.randomUUID().toString());

        User seller = VALID_SELLER;
        seller.setToAwaitingForApproval();

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);
        Mockito.doNothing().when(policy).enforce(seller, "APPROVE_SELLER-001");
        Mockito.when(userGateway.findSellerById(Mockito.any())).thenReturn(Optional.of(seller));
        usecase.execute(input);

        Mockito.verify(authenticationGateway, Mockito.times(1)).loggedUser();
        Mockito.verify(policy, Mockito.times(1)).enforce(seller, "APPROVE_SELLER-001");
        Mockito.verify(userGateway, Mockito.times(1)).findSellerById(Mockito.any(UUID.class));
        Mockito.verify(userGateway, Mockito.times(1)).save(seller);
    }
}
