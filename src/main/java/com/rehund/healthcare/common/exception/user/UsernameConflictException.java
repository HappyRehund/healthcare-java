package com.rehund.healthcare.common.exception.user;

public class UsernameConflictException extends RuntimeException {
    public UsernameConflictException(String message) {
        super(message);
    }
}
