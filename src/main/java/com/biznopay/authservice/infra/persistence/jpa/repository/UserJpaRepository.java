package com.biznopay.authservice.infra.persistence.jpa.repository;

import org.springframework.stereotype.Component;

@Component
public interface UserJpaRepository {
    long countSAs();
}
