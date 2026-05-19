package com.biznopay.authservice.domain.vo;

import com.biznopay.authservice.domain.exception.InvalidNuitException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

public record Nuit(String value) {
    public Nuit {
        if (value == null || value.isBlank()) {
            throw new RequiredFieldException("NUIT", Nuit.class.getName(), "NUIT-001");
        }
        if (!value.matches("^\\d+$")) {
            throw new InvalidNuitException(Nuit.class.getName(), "NUIT-002");
        }
        if (!value.matches("^\\d{9}$")) {
            throw new InvalidNuitException(Nuit.class.getName(), "NUIT-003");
        }
    }
}