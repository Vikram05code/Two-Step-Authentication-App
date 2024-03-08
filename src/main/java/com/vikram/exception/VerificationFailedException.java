package com.vikram.exception;

public class VerificationFailedException extends RuntimeException {
    public VerificationFailedException(String message) {
        super(message);
    }
}