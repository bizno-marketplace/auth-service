package com.biznopay.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EntityScan(basePackages = {
        "com.biznopay.authservice.infra.persistence.jpa.entity",
        "com.biznopay.commons.outbox.persistence.jpa.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.biznopay.authservice.infra.persistence.jpa.repository",
        "com.biznopay.commons.outbox.persistence.jpa.repository"
})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
