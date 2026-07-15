package com.biznopay.authservice.usecase.saller;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.seller.Seller;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.AccessDeniedException;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.policy.UpdateSellerPolicy;
import com.biznopay.authservice.infra.gateway.TransactionGatewayImpl;
import com.biznopay.authservice.usecase.seller.update.UpdateSeller;
import com.biznopay.authservice.usecase.seller.update.UpdateSellerInput;
import com.biznopay.authservice.usecase.seller.update.UpdateSellerOutput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.biznopay.authservice.testcases.BuyerTestCases.VALID_BUYER_DEFINED_ADDRESS;
import static com.biznopay.authservice.testcases.SellerTestCases.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class UpdateSellerTest {
    @Mock
    private AuthenticationGateway authenticationGateway;
    @Mock
    private UserGateway userGateway;
    @Mock
    private StorageGateway storageGateway;
    @Mock
    private ActivationTokenGateway activationTokenGateway;
    @Mock
    private DomainEventGateway domainEventGateway;

    private TransactionGateway transactionGateway = new TransactionGatewayImpl();

    private UpdateSellerPolicy updateSellerPolicy = new UpdateSellerPolicy();


    private UpdateSeller setUp() {
        return new UpdateSeller(transactionGateway, authenticationGateway,
                updateSellerPolicy, userGateway, activationTokenGateway, domainEventGateway);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if requesting user is not seller")
    public void shouldThrowAccessDeniedExceptionIRequestingUserIsNotSeller() {
        User buyer = VALID_BUYER_DEFINED_ADDRESS();
        UpdateSellerInput input = new UpdateSellerInput(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE,
                VALID_STORE_NAME, VALID_STORE_DESC);

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(buyer);
        UpdateSeller updateSeller = setUp();
        Assertions.assertThatThrownBy(() -> updateSeller.execute(input))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied");
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if requesting seller is in invalid status")
    public void shouldThrowAccessDeniedExceptionIfRequestingSellerIsNotRejected() {
        User seller = validaSeller();
        seller.block();

        UpdateSellerInput input = new UpdateSellerInput(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE,
                VALID_STORE_NAME, VALID_STORE_DESC);

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);
        UpdateSeller updateSeller = setUp();
        Assertions.assertThatThrownBy(() -> updateSeller.execute(input))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied");
    }

    @Test
    @DisplayName("Should update only first name when other fields are null")
    public void shouldUpdateOnlyFirstName() {
        User seller = validaSeller();

        UpdateSellerInput input = new UpdateSellerInput(VALID_FIRST_NAME, null, null, null,
                null, null);

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        UpdateSeller UpdateSeller = setUp();
        UpdateSellerOutput output = UpdateSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getFirstName().equals(VALID_FIRST_NAME) &&
                        u.getLastName().equals(seller.getLastName())
        ));
        Assertions.assertThat(output).isNotNull();
    }

    @Test
    @DisplayName("Should update only last name when other fields are null")
    public void shouldUpdateOnlyLastName() {
        User seller = validaSeller();

        UpdateSellerInput input = new UpdateSellerInput(
                null, "NovoApelido", null, null,
                null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        UpdateSeller updateSeller = setUp();
        updateSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getLastName().equals("NovoApelido") &&
                        u.getFirstName().equals(seller.getFirstName())
        ));
    }

    @Test
    @DisplayName("Should update only phone when other fields are null")
    public void shouldUpdateOnlyPhone() {
        User seller = validaSeller();

        UpdateSellerInput input = new UpdateSellerInput(
                null, null, null, "+258841234567",
                null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        UpdateSeller updateSeller = setUp();
        updateSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getPhone().equals("+258841234567")
        ));
    }

    @Test
    @DisplayName("Should update only store name when other fields are null")
    public void shouldUpdateOnlyStoreName() {
        User seller = validaSeller();

        UpdateSellerInput input = new UpdateSellerInput(
                null, null, null, null,
                "Nova Loja XYZ", null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        UpdateSeller updateSeller = setUp();
        updateSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                ((Seller) u).getStoreName().equals("Nova Loja XYZ")
        ));
    }

    @Test
    @DisplayName("Should update only store description when other fields are null")
    public void shouldUpdateOnlyStoreDescription() {
        User seller = validaSeller();

        UpdateSellerInput input = new UpdateSellerInput(
                null, null, null, null,
                null, "Nova descricao da loja"
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        UpdateSeller updateSeller = setUp();
        updateSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                ((Seller) u).getStoreDescription().equals("Nova descricao da loja")
        ));
    }


    @Test
    @DisplayName("Should set status to PENDING when email is changed")
    public void shouldSetStatusToPendingConfirmationWhenEmailChanged() {
        User seller = validaSeller();
        UpdateSellerInput input = new UpdateSellerInput(
                null, null, "novoemail@bizno.co.mz", null,
                null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        UpdateSeller updateSeller = setUp();
        UpdateSellerOutput output = updateSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getStatus() == UserStatus.PENDING
        ));
        Assertions.assertThat(output.message())
                .contains("changed you email");
    }

    @Test
    @DisplayName("Should set status to AWAITING_APPROVAL when email is not changed")
    public void shouldSetStatusToAwaitingApprovalWhenEmailNotChanged() {
        User seller = validaSeller();
        UpdateSellerInput input = new UpdateSellerInput(
                null, null, null, null,
                "Nova Loja ABC", null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        UpdateSeller updateSeller = setUp();
        UpdateSellerOutput output = updateSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getStatus() == UserStatus.AWAITING_APPROVAL
        ));
        Assertions.assertThat(output.message())
                .contains("updated successfully");
    }
}
