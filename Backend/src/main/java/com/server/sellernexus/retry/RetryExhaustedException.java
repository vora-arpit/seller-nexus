package com.server.sellernexus.retry;

public class RetryExhaustedException extends RuntimeException {
    public RetryExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }
}
