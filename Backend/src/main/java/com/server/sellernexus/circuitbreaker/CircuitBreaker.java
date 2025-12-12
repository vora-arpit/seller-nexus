package com.server.sellernexus.circuitbreaker;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

public class CircuitBreaker {
    private final String serviceName;
    private volatile CircuitState state = CircuitState.CLOSED;
    private final int failureThreshold;
    private final Duration timeout;
    private int failureCount = 0;
    private Instant lastFailureTime;

    public CircuitBreaker(String serviceName, int failureThreshold, Duration timeout) {
        this.serviceName = serviceName;
        this.failureThreshold = failureThreshold;
        this.timeout = timeout;
    }

    public <T> T execute(Callable<T> operation) throws Exception {
        if (state == CircuitState.OPEN) {
            if (Instant.now().isAfter(lastFailureTime.plus(timeout))) {
                state = CircuitState.HALF_OPEN;
                System.out.println("[CircuitBreaker] " + serviceName + " transitioning to HALF_OPEN");
            } else {
                throw new CircuitBreakerOpenException(serviceName);
            }
        }

        try {
            T result = operation.call();
            recordSuccess();
            return result;
        } catch (Exception e) {
            recordFailure();
            throw e;
        }
    }

    public synchronized void recordSuccess() {
        failureCount = 0;
        if (state == CircuitState.HALF_OPEN) {
            state = CircuitState.CLOSED;
            System.out.println("[CircuitBreaker] " + serviceName + " recovered to CLOSED");
        }
    }

    public synchronized void recordFailure() {
        failureCount++;
        lastFailureTime = Instant.now();
        
        System.out.println("[CircuitBreaker] " + serviceName + " failure recorded. Count: " + failureCount + "/" + failureThreshold);

        if (failureCount >= failureThreshold && state != CircuitState.OPEN) {
            state = CircuitState.OPEN;
            System.out.println("[CircuitBreaker] " + serviceName + " OPENED after " + failureCount + " failures");
        }
    }

    public synchronized void reset() {
        failureCount = 0;
        state = CircuitState.CLOSED;
        lastFailureTime = null;
        System.out.println("[CircuitBreaker] " + serviceName + " manually reset to CLOSED");
    }

    public CircuitState getState() {
        return state;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Instant getLastFailureTime() {
        return lastFailureTime;
    }

    public String getStats() {
        return String.format("Service: %s, State: %s, Failures: %d/%d, Last Failure: %s",
                serviceName, state, failureCount, failureThreshold,
                lastFailureTime != null ? lastFailureTime.toString() : "None");
    }
}
