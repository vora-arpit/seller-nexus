package com.server.sellernexus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when API credentials are invalid or unauthorized
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialException extends RuntimeException {
    
    public InvalidCredentialException(String message) {
        super(message);
    }
    
    public InvalidCredentialException(String message, Throwable cause) {
        super(message, cause);
    }
}
