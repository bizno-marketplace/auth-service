package com.biznopay.authservice;

import com.biznopay.authservice.domain.entity.user.Address;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.infra.helper.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DevDataSeeder implements ApplicationRunner {

    private final UserGateway userGateway;
    private final JwtHelper jwtHelper;

    @Override
    public void run(ApplicationArguments args) {
        Address address = Address.of(-25.9692, 32.5732, "Av. 24 de Julho",
                "Sommerschield", "Maputo", "Maputo", "Mozambique");

        User buyer = Buyer.register(
                "João", "Tembe", "joao.tembe@gmail.com",
                "+258841234567", "Segura@123", address
        );
        buyer.activate();
        userGateway.save(buyer);
        String token = jwtHelper.generate(buyer);
        System.out.println("TOKEN: " + token);
    }
}