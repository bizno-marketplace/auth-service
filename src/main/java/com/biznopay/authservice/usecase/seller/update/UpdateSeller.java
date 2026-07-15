package com.biznopay.authservice.usecase.seller.update;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserUpdated;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.seller.Seller;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.policy.UpdateSellerPolicy;

import java.time.LocalDateTime;
import java.util.Objects;

public class UpdateSeller {
    private final TransactionGateway transactionGateway;
    private final AuthenticationGateway authenticationGateway;
    private final UpdateSellerPolicy policy;
    private final UserGateway userGateway;
    private final ActivationTokenGateway activationTokenGateway;
    private final DomainEventGateway domainEventGateway;

    public UpdateSeller(TransactionGateway transactionGateway, AuthenticationGateway authenticationGateway,
                        UpdateSellerPolicy policy, UserGateway userGateway, ActivationTokenGateway activationTokenGateway, DomainEventGateway domainEventGateway) {
        this.transactionGateway = transactionGateway;
        this.authenticationGateway = authenticationGateway;
        this.policy = policy;
        this.userGateway = userGateway;
        this.activationTokenGateway = activationTokenGateway;
        this.domainEventGateway = domainEventGateway;
    }

    public UpdateSellerOutput execute(UpdateSellerInput input) {
        return transactionGateway.execute(() -> {
            User seller = authenticationGateway.loggedUser();
            policy.enforce(seller, "UPDATE_SELLER-001");
            User updatedSeller = updateSellerInfo(seller, input);
            return resendEmailValidation(seller, updatedSeller);
        });
    }

    private Seller updateSellerInfo(User seller, UpdateSellerInput input) {
        Seller currentSeller = (Seller) seller;

        String firstName = input.firstName() != null ? input.firstName() : seller.getFirstName();
        String lastName = input.lastName() != null ? input.lastName() : seller.getLastName();
        String email = input.email() != null ? input.email() : seller.getEmail();
        String phone = input.phoneNumber() != null ? input.phoneNumber() : seller.getPhone();
        String storeName = input.storeName() != null ? input.storeName() : currentSeller.getStoreName();
        String storeDescription = input.storeDescription() != null ? input.storeDescription() : currentSeller.getStoreDescription();

        return Seller.reconstruct(
                seller.getId(), firstName, lastName, email, phone,
                seller.getPassword(), seller.getStatus(), seller.getExpiresAt(),
                seller.getCreatedAt(), LocalDateTime.now(), storeName,
                storeDescription, currentSeller.getNuit(), currentSeller.getStoreAddress(),
                currentSeller.getBiDocument()
        );
    }

    private UpdateSellerOutput resendEmailValidation(User seller, User updatedSeller) {
        if (!Objects.equals(seller.getEmail(), updatedSeller.getEmail())) {
            ActivationToken token = ActivationToken.generate(seller.getId());
            updatedSeller.setToPending();
            userGateway.update(updatedSeller);
            activationTokenGateway.save(token);
            UserUpdated event = UserUpdated.of(seller.getId(), seller.getEmail(), seller.getFirstName(), token.getId());
            domainEventGateway.publish(event);
            return new UpdateSellerOutput("As you changed you email we've sent instruction to conform account in the provided email.");
        }

        updatedSeller.setToAwaitingForApproval();
        userGateway.update(updatedSeller);
        return new UpdateSellerOutput("Seller updated successfully");
    }
}
