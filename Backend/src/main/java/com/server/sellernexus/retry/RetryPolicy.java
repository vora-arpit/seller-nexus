package com.server.sellernexus.retry;

import java.util.concurrent.Callable;

public interface RetryPolicy {
    <T> T execute(Callable<T> operation) throws Exception;
    boolean shouldRetry(int attempt);
    long getDelay(int attempt);
}
