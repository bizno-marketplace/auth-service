package com.biznopay.authservice.usecase.saller;

import com.biznopay.authservice.domain.entity.user.Address;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.seller.Seller;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.AccessDeniedException;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.policy.ResubmitSellerPolicy;
import com.biznopay.authservice.infra.gateway.TransactionGatewayImpl;
import com.biznopay.authservice.usecase.seller.resubmitseller.ResubmitSeller;
import com.biznopay.authservice.usecase.seller.resubmitseller.ResubmitSellerInput;
import com.biznopay.authservice.usecase.seller.resubmitseller.ResubmitSellerOutput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.biznopay.authservice.testcases.BuyerTestCases.VALID_BUYER;
import static com.biznopay.authservice.testcases.SellerTestCases.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ResubmitSellerTests {
    @Mock
    AuthenticationGateway authenticationGateway;
    @Mock
    UserGateway userGateway;
    @Mock
    StorageGateway storageGateway;
    @Mock
    ActivationTokenGateway activationTokenGateway;
    @Mock
    DomainEventGateway domainEventGateway;
    private TransactionGateway transactionGateway = new TransactionGatewayImpl();
    private ResubmitSellerPolicy resubmitSellerPolicy = new ResubmitSellerPolicy();

    private ResubmitSeller setUp() {
        return new ResubmitSeller(transactionGateway, resubmitSellerPolicy, authenticationGateway, userGateway,
                storageGateway, activationTokenGateway, domainEventGateway);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if requesting user is not seller")
    public void shouldThrowAccessDeniedExceptionIRequestingUserIsNotSeller() {
        User buyer = VALID_BUYER;
        ResubmitSellerInput input = new ResubmitSellerInput(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE,
                VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI_REQUEST);

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(buyer);
        ResubmitSeller resubmitSeller = setUp();
        Assertions.assertThatThrownBy(() -> resubmitSeller.execute(input))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied");
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if requesting seller is not rejected")
    public void shouldThrowAccessDeniedExceptionIfRequestingSellerIsNotRejected() {
        User seller = VALID_SELLER;
        seller.setToAwaitingForApproval();

        ResubmitSellerInput input = new ResubmitSellerInput(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE,
                VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI_REQUEST);

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);
        ResubmitSeller resubmitSeller = setUp();
        Assertions.assertThatThrownBy(() -> resubmitSeller.execute(input))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied");
    }

    @Test
    @DisplayName("Should update only first name when other fields are null")
    public void shouldUpdateOnlyFirstName() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                "NovoNome", null, null, null,
                null, null, null, null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        ResubmitSellerOutput output = resubmitSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getFirstName().equals("NovoNome") &&
                        u.getLastName().equals(seller.getLastName())
        ));
        Assertions.assertThat(output).isNotNull();
    }

    @Test
    @DisplayName("Should update only last name when other fields are null")
    public void shouldUpdateOnlyLastName() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, "NovoApelido", null, null,
                null, null, null, null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        resubmitSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getLastName().equals("NovoApelido") &&
                        u.getFirstName().equals(seller.getFirstName())
        ));
    }

    @Test
    @DisplayName("Should update only phone when other fields are null")
    public void shouldUpdateOnlyPhone() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, null, null, "+258841234567",
                null, null, null, null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        resubmitSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getPhone().equals("+258841234567")
        ));
    }

    @Test
    @DisplayName("Should update only store name when other fields are null")
    public void shouldUpdateOnlyStoreName() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, null, null, null,
                "Nova Loja XYZ", null, null, null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        resubmitSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                ((Seller) u).getStoreName().equals("Nova Loja XYZ")
        ));
    }

    @Test
    @DisplayName("Should update only store description when other fields are null")
    public void shouldUpdateOnlyStoreDescription() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, null, null, null,
                null, "Nova descricao da loja", null, null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        resubmitSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                ((Seller) u).getStoreDescription().equals("Nova descricao da loja")
        ));
    }

    @Test
    @DisplayName("Should update only nuit when other fields are null")
    public void shouldUpdateOnlyNuit() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, null, null, null,
                null, null, "987654321", null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        resubmitSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                ((Seller) u).getNuit().equals("987654321")
        ));
    }

    @Test
    @DisplayName("Should update only store address when other fields are null")
    public void shouldUpdateOnlyStoreAddress() {
        User seller = VALID_SELLER;
        seller.reject();

        Address newAddress = Address.of(-25.969, 32.573, "Rua Nova", "Polana", "Maputo", "Maputo", "Mozambique");

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, null, null, null,
                null, null, null, newAddress, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        resubmitSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                ((Seller) u).getStoreAddress().equals(newAddress)
        ));
    }

    @Test
    @DisplayName("Should upload new BI document when provided")
    public void shouldUploadNewBiDocumentWhenProvided() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, null, null, null,
                null, null, VALID_NUIT, null, VALID_BI_REQUEST
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        resubmitSeller.execute(input);

        Mockito.verify(storageGateway).upload(Mockito.anyList());
    }

    @Test
    @DisplayName("Should not upload BI document when not provided")
    public void shouldNotUploadBiDocumentWhenNotProvided() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, null, null, null,
                null, null, null, null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        resubmitSeller.execute(input);

        Mockito.verify(storageGateway, Mockito.never()).upload(Mockito.anyList());
    }

    @Test
    @DisplayName("Should set status to PENDING when email is changed")
    public void shouldSetStatusToPendingConfirmationWhenEmailChanged() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, null, "novoemail@bizno.co.mz", null,
                null, null, null, null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        ResubmitSellerOutput output = resubmitSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getStatus() == UserStatus.PENDING
        ));
        Assertions.assertThat(output.message())
                .contains("changed you email");
    }

    @Test
    @DisplayName("Should set status to AWAITING_APPROVAL when email is not changed")
    public void shouldSetStatusToAwaitingApprovalWhenEmailNotChanged() {
        User seller = VALID_SELLER;
        seller.reject();

        ResubmitSellerInput input = new ResubmitSellerInput(
                null, null, null, null,
                "Nova Loja ABC", null, null, null, null
        );

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(seller);

        ResubmitSeller resubmitSeller = setUp();
        ResubmitSellerOutput output = resubmitSeller.execute(input);

        Mockito.verify(userGateway).update(Mockito.argThat(u ->
                u.getStatus() == UserStatus.AWAITING_APPROVAL
        ));
        Assertions.assertThat(output.message())
                .contains("resubmitted successfully");
    }
}
