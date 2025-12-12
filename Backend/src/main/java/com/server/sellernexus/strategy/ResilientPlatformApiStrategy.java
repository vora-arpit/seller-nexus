package com.server.sellernexus.strategy;

import com.server.sellernexus.circuitbreaker.CircuitBreaker;
import com.server.sellernexus.circuitbreaker.CircuitBreakerRegistry;
import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.model.sellurNexus.Product;
import com.server.sellernexus.retry.RetryPolicy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ResilientPlatformApiStrategy implements PlatformApiStrategy {
    private final PlatformApiStrategy delegate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryPolicy retryPolicy;

    public ResilientPlatformApiStrategy(JoomApiStrategy joomApiStrategy,
                                       CircuitBreakerRegistry circuitBreakerRegistry,
                                       RetryPolicy retryPolicy) {
        this.delegate = joomApiStrategy;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryPolicy = retryPolicy;
    }

    @Override
    public Map<String, Object> fetchProducts(PlatformCredential credential, int page, int pageSize) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("JOOM-API");
        
        try {
            return circuitBreaker.execute(() ->
                retryPolicy.execute(() ->
                    delegate.fetchProducts(credential, page, pageSize)
                )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> fetchProductById(PlatformCredential credential, String productId) throws Exception {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("JOOM-API");
        
        return circuitBreaker.execute(() ->
            retryPolicy.execute(() ->
                delegate.fetchProductById(credential, productId)
            )
        );
    }

    @Override
    public Map<String, Object> createProduct(PlatformCredential credential, Map<String, Object> productData) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("JOOM-API");
        
        try {
            return circuitBreaker.execute(() ->
                retryPolicy.execute(() ->
                    delegate.createProduct(credential, productData)
                )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> updateProduct(PlatformCredential credential, String productId, Map<String, Object> productData) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("JOOM-API");
        
        try {
            return circuitBreaker.execute(() ->
                retryPolicy.execute(() ->
                    delegate.updateProduct(credential, productId, productData)
                )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteProduct(PlatformCredential credential, String productId) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("JOOM-API");
        
        try {
            return circuitBreaker.execute(() ->
                retryPolicy.execute(() ->
                    delegate.deleteProduct(credential, productId)
                )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean validateCredentials(PlatformCredential credential) {
        return delegate.validateCredentials(credential);
    }

    @Override
    public String getPlatformName() {
        return delegate.getPlatformName();
    }
}
