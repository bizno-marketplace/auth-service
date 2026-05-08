package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class NonBiznoInstitutionalEmailException extends BiznoException {
    public NonBiznoInstitutionalEmailException(String code) {
        String message = "E-mail must be a bizno institutional email";
        super(message, code, ExceptionSeverity.LOW, Map.of(User.class.getName(), "E-mail"));
    }
}
