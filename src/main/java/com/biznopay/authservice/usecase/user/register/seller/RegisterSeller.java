package com.biznopay.authservice.usecase.user.register.seller;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.Seller;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.exception.NuitAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.util.DocumentPathGenerator;
import com.biznopay.authservice.domain.vo.BiDocument;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;
import com.biznopay.authservice.domain.vo.StorageFile;

import java.util.List;
import java.util.Optional;

import static com.biznopay.authservice.domain.util.DomainFuncUtils.validatePassword;

public class RegisterSeller {
    private final UserGateway userGateway;
    private final EncoderGateway encoderGateway;
    private final StorageGateway storageGateway;
    private final DomainEventGateway domainEventGateway;
    private final ActivationTokenGateway activationTokenGateway;

    public RegisterSeller(UserGateway userGateway, EncoderGateway encoderGateway, StorageGateway storageGateway,
                          DomainEventGateway domainEventGateway, ActivationTokenGateway activationTokenGateway) {
        this.userGateway = userGateway;
        this.encoderGateway = encoderGateway;
        this.storageGateway = storageGateway;
        this.domainEventGateway = domainEventGateway;
        this.activationTokenGateway = activationTokenGateway;
    }

    public RegisterSellerOutput execute(RegisterSellerInput input) {
        validateSeller(input);
        String rawPassword = validatePassword(input.password(), "Seller", "REGISTER_SELLER-003");
        String encodedPassword = encoderGateway.encode(rawPassword);
        BiDocument biDocument = getBiDocument(input.nuit(), input.biDocument());
        List<StorageFile> files = buildFiles(biDocument, input);
        storageGateway.upload(files);
        User seller = Seller.register(input.firstName(), input.lastName(), input.email(), input.phoneNumber(),
                encodedPassword, input.storeName(), input.nuit(), input.nuit(), input.storeAddress(), biDocument);
        userGateway.save(seller);
        ActivationToken token = ActivationToken.generate(seller.getId());
        activationTokenGateway.save(token);
        UserRegistered event = UserRegistered.of(seller.getId(), seller.getEmail(), seller.getFirstName(), token.getId());
        domainEventGateway.publish(event);
        return new RegisterSellerOutput("We've sent an activation link to provided email: " + input.email());
    }

    private void validateSeller(RegisterSellerInput input) {
        Optional<User> optUser = userGateway.findByEmail(input.email());
        if (optUser.isPresent())
            throw new EmailAlreadyInUseException("REGISTER_SELLER-001");
        optUser = userGateway.findByNuit(input.nuit());
        if (optUser.isPresent())
            throw new NuitAlreadyInUseException("REGISTER_SELLER-002");
    }

    private BiDocument getBiDocument(String nuit, BiDocumentRequest biDocument) {
        return DocumentPathGenerator.generateBiDocument(nuit, biDocument.frontPhotoExt(), biDocument.backPhotoExt());
    }

    private List<StorageFile> buildFiles(BiDocument biDocument, RegisterSellerInput input) {
        return List.of(
                new StorageFile(input.biDocument().frontPhotoBytes(), biDocument.getFrontPath()),
                new StorageFile(input.biDocument().backPhotoBytes(), biDocument.getBackPath())
        );
    }
}
