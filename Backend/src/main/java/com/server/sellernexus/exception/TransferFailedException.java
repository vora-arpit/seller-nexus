package com.server.sellernexus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when product transfer between platforms fails
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TransferFailedException extends RuntimeException {
    
    public TransferFailedException(String message) {
        super(message);
    }
    
    public TransferFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
