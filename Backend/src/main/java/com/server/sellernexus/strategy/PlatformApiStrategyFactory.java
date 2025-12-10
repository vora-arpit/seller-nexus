package com.server.sellernexus.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================
 * STRATEGY DESIGN PATTERN - Strategy Factory
 * ============================================
 * 
 * PlatformApiStrategyFactory - Factory for selecting platform strategies
 * 
 * Purpose: Select and provide the appropriate platform strategy at runtime
 * Benefits:
 * - Centralized strategy selection logic
 * - Auto-registration of all strategies via Spring
 * - Easy to add new platforms without modifying factory
 * - Type-safe strategy retrieval
 * 
 * Pattern Type: Strategy Pattern + Factory Pattern combination
 * 
 * This factory automatically discovers all PlatformApiStrategy
 * implementations registered in Spring and makes them available
 * for runtime selection based on platform name
 * 
 * Usage Example:
 * PlatformApiStrategy strategy = factory.getStrategy("JOOM");
 * Map<String, Object> products = strategy.fetchProducts(credential, 1, 50);
 * 
 * // Later, switch to different platform
 * strategy = factory.getStrategy("AMAZON");
 * products = strategy.fetchProducts(credential, 1, 50);
 */
@Component
@RequiredArgsConstructor
public class PlatformApiStrategyFactory {
    
    // Spring auto-injects all implementations of PlatformApiStrategy
    private final List<PlatformApiStrategy> strategies;
    private Map<String, PlatformApiStrategy> strategyMap;
    
    /**
     * Initialize strategy map with all available strategies
     * Called automatically by Spring after dependency injection
     */
    @javax.annotation.PostConstruct
    public void init() {
        strategyMap = new HashMap<>();
        for (PlatformApiStrategy strategy : strategies) {
            strategyMap.put(strategy.getPlatformName().toUpperCase(), strategy);
        }
        
        System.out.println("[PlatformApiStrategyFactory] Registered strategies: " + strategyMap.keySet());
    }
    
    /**
     * Get the appropriate strategy for a platform
     * @param platformName Platform identifier (e.g., "JOOM", "AMAZON")
     * @return Platform-specific strategy implementation
     * @throws IllegalArgumentException if platform not supported
     */
    public PlatformApiStrategy getStrategy(String platformName) {
        if (platformName == null) {
            throw new IllegalArgumentException("Platform name cannot be null");
        }
        
        PlatformApiStrategy strategy = strategyMap.get(platformName.toUpperCase());
        
        if (strategy == null) {
            throw new IllegalArgumentException(
                String.format("No strategy found for platform: %s. Available platforms: %s",
                    platformName, strategyMap.keySet())
            );
        }
        
        return strategy;
    }
    
    /**
     * Check if a platform is supported
     * @param platformName Platform identifier
     * @return true if strategy exists for this platform
     */
    public boolean isSupported(String platformName) {
        return platformName != null && strategyMap.containsKey(platformName.toUpperCase());
    }
    
    /**
     * Get all supported platform names
     * @return Set of supported platform identifiers
     */
    public java.util.Set<String> getSupportedPlatforms() {
        return strategyMap.keySet();
    }
}
