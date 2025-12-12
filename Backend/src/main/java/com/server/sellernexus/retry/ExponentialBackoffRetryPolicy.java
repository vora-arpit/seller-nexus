package com.server.sellernexus.retry;

import org.springframework.stereotype.Component;
import java.util.concurrent.Callable;

@Component
public class ExponentialBackoffRetryPolicy implements RetryPolicy {
    private final int maxAttempts;
    private final long baseDelay;

    public ExponentialBackoffRetryPolicy() {
        this.maxAttempts = 5;
        this.baseDelay = 1000; // 1 second
    }

    @Override
    public <T> T execute(Callable<T> operation) throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                System.out.println("[RetryPolicy] Attempt " + attempt + "/" + maxAttempts);
                return operation.call();
            } catch (Exception e) {
                lastException = e;
                System.out.println("[RetryPolicy] Attempt " + attempt + " failed: " + e.getMessage());
                
                // Don't retry client errors (4xx) - these are permanent failures
                if (isClientError(e)) {
                    System.out.println("[RetryPolicy] Client error detected - not retrying");
                    throw e;
                }
                
                if (attempt < maxAttempts) {
                    long delay = getDelay(attempt);
                    System.out.println("[RetryPolicy] Retrying in " + delay + "ms...");
                    Thread.sleep(delay);
                }
            }
        }
        
        System.out.println("[RetryPolicy] All " + maxAttempts + " attempts exhausted");
        throw new RetryExhaustedException("All retry attempts failed", lastException);
    }
    
    private boolean isClientError(Exception e) {
        // Check if it's a Spring HttpClientErrorException (4xx errors)
        String className = e.getClass().getName();
        if (className.contains("HttpClientErrorException")) {
            return true;
        }
        // Check if it's wrapped in RuntimeException
        if (e.getCause() != null) {
            String causeClassName = e.getCause().getClass().getName();
            return causeClassName.contains("HttpClientErrorException");
        }
        return false;
    }

    @Override
    public boolean shouldRetry(int attempt) {
        return attempt < maxAttempts;
    }

    @Override
    public long getDelay(int attempt) {
        return baseDelay * (long) Math.pow(2, attempt - 1);
    }
}
