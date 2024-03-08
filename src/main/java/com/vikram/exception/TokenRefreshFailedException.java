package com.vikram.exception;

public class TokenRefreshFailedException extends RuntimeException {
    public TokenRefreshFailedException(String message) {
        super(message);
    }
}
