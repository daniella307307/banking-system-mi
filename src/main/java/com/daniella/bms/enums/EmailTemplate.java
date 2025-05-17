package com.daniella.bms.enums;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    RESET_PASSWORD("forgot-password-email"),
    ACTIVATE_ACCOUNT("verify-account-email"),
    ACCOUNT_VERIFIED("account-verification-successful"),
    PASSWORD_RESET_SUCCESS("password-reset-successful"),
    TRANSACTION_NOTIFICATION("transaction-notification"),
    ACCOUNT_VERIFICATION("account_verification");

    private final String name;

    EmailTemplate(String name) {
        this.name = name;
    }
}
