package com.server.sellernexus.circuitbreaker;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CircuitBreakerRegistry {
    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("[CircuitBreakerRegistry] Initializing circuit breakers...");
        getOrCreate("JOOM-API");
        System.out.println("[CircuitBreakerRegistry] Created JOOM-API circuit breaker");
    }

    public CircuitBreaker getOrCreate(String serviceName) {
        return circuitBreakers.computeIfAbsent(serviceName, name -> {
            System.out.println("[CircuitBreakerRegistry] Creating circuit breaker for: " + name);
            return new CircuitBreaker(name, 5, Duration.ofSeconds(60));
        });
    }

    public void reset(String serviceName) {
        CircuitBreaker breaker = circuitBreakers.get(serviceName);
        if (breaker != null) {
            breaker.reset();
        }
    }

    public void resetAll() {
        circuitBreakers.values().forEach(CircuitBreaker::reset);
    }

    public Map<String, CircuitBreaker> getAllBreakers() {
        return circuitBreakers;
    }

    public Map<String, String> getAllStats() {
        Map<String, String> stats = new ConcurrentHashMap<>();
        circuitBreakers.forEach((name, breaker) -> stats.put(name, breaker.getStats()));
        return stats;
    }
}
