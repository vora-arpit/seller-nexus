package com.server.sellernexus.circuitbreaker;

public class CircuitBreakerOpenException extends RuntimeException {
    private final String serviceName;

    public CircuitBreakerOpenException(String serviceName) {
        super("Circuit breaker is OPEN for service: " + serviceName);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
