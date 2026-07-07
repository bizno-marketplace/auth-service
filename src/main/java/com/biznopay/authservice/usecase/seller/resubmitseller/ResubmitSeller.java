package com.biznopay.authservice.usecase.seller.resubmitseller;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserUpdated;
import com.biznopay.authservice.domain.entity.user.Address;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.seller.Seller;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.policy.ResubmitSellerPolicy;
import com.biznopay.authservice.domain.util.DocumentPathGenerator;
import com.biznopay.authservice.domain.vo.BiDocument;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;
import com.biznopay.authservice.domain.vo.StorageFile;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ResubmitSeller {
    private final TransactionGateway transactionGateway;
    private final ResubmitSellerPolicy sellerPolicy;
    private final AuthenticationGateway authenticationGateway;
    private final UserGateway userGateway;
    private final StorageGateway storageGateway;
    private final ActivationTokenGateway activationTokenGateway;
    private final DomainEventGateway domainEventGateway;
    private final MetricsGateway metricsGateway;

    public ResubmitSellerOutput execute(ResubmitSellerInput input) {
        return transactionGateway.execute(() -> {
            User requestingUser = authenticationGateway.loggedUser();
            sellerPolicy.enforce(requestingUser, "RESUBMIT_SELLER-001");
            User updatedSeller = updateSellerInfo(requestingUser, input);
            metricsGateway.incrementSellerResubmitted();
            return resendEmailValidation(requestingUser, updatedSeller);
        });
    }

    private Seller updateSellerInfo(User seller, ResubmitSellerInput input) {
        Seller currentSeller = (Seller) seller;

        String firstName = input.firstName() != null ? input.firstName() : seller.getFirstName();
        String lastName = input.lastName() != null ? input.lastName() : seller.getLastName();
        String email = input.email() != null ? input.email() : seller.getEmail();
        String phone = input.phoneNumber() != null ? input.phoneNumber() : seller.getPhone();
        String storeName = input.storeName() != null ? input.storeName() : currentSeller.getStoreName();
        String storeDescription = input.storeDescription() != null ? input.storeDescription() : currentSeller.getStoreDescription();
        String nuit = input.nuit() != null ? input.nuit() : currentSeller.getNuit();
        Address storeAddress = input.storeAddress() != null ? input.storeAddress() : currentSeller.getStoreAddress();
        BiDocument biDocument = input.biDocument() != null
                ? getBiDocument(input.nuit(), input.biDocument())
                : currentSeller.getBiDocument();

        if (input.biDocument() != null) {
            List<StorageFile> files = buildFiles(biDocument, input);
            storageGateway.upload(files);
        }

        return Seller.reconstruct(
                seller.getId(), firstName, lastName, email, phone,
                seller.getPassword(), seller.getStatus(), seller.getExpiresAt(),
                seller.getCreatedAt(), LocalDateTime.now(), storeName,
                storeDescription, nuit, storeAddress, biDocument
        );
    }

    private List<StorageFile> buildFiles(BiDocument biDocument, ResubmitSellerInput input) {
        return List.of(
                new StorageFile(input.biDocument().frontPhotoBytes(), biDocument.getFrontPath()),
                new StorageFile(input.biDocument().backPhotoBytes(), biDocument.getBackPath())
        );
    }

    private BiDocument getBiDocument(String nuit, BiDocumentRequest biDocument) {
        return DocumentPathGenerator.generateBiDocument(nuit, biDocument.frontPhotoExt(), biDocument.backPhotoExt());
    }

    private ResubmitSellerOutput resendEmailValidation(User seller, User updatedSeller) {
        if (!Objects.equals(seller.getEmail(), updatedSeller.getEmail())) {
            ActivationToken token = ActivationToken.generate(seller.getId());
            updatedSeller.setToPending();
            userGateway.update(updatedSeller);
            activationTokenGateway.save(token);
            UserUpdated event = UserUpdated.of(seller.getId(), seller.getEmail(), seller.getFirstName(), token.getId());
            domainEventGateway.publish(event);
            return new ResubmitSellerOutput("As you changed you email we've sent instruction to conform account in the provided email.");
        }

        updatedSeller.setToAwaitingForApproval();
        userGateway.update(updatedSeller);
        return new ResubmitSellerOutput("Seller resubmitted successfully");
    }
}