package com.biznopay.authservice.domain.util;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.InvalidPasswordException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

public class DomainFuncUtils {
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&._#-])[A-Za-z\\d@$!%*?&._#-]{8,}$";

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty())
            throw new RequiredFieldException("Password", User.class.getName(), "REGISTER_SA-003");
        if (!password.matches(PASSWORD_REGEX))
            throw new InvalidPasswordException("REGISTER_SA-004");
        return password;
    }
}
