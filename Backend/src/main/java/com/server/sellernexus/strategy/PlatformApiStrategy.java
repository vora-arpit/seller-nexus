package com.server.sellernexus.strategy;

import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import java.util.Map;

/**
 * ============================================
 * STRATEGY DESIGN PATTERN - Strategy Interface
 * ============================================
 * 
 * PlatformApiStrategy - Strategy interface for different platform APIs
 * 
 * Purpose: Define a family of algorithms (platform-specific API operations)
 *          and make them interchangeable
 * 
 * Benefits:
 * - Open/Closed Principle: Add new platforms without modifying existing code
 * - Single Responsibility: Each platform has its own implementation
 * - Runtime Selection: Choose platform strategy at runtime
 * - Testability: Easy to mock and test different platform behaviors
 * 
 * Pattern Type: Strategy Pattern - Strategy Interface
 * 
 * This interface defines the contract that all platform-specific
 * implementations must follow (Joom, Amazon, eBay, Shopify, etc.)
 * 
 * Usage Example:
 * PlatformApiStrategy strategy = strategyFactory.getStrategy("JOOM");
 * Map<String, Object> products = strategy.fetchProducts(credential, 1, 50);
 * Map<String, Object> result = strategy.createProduct(credential, productData);
 */
public interface PlatformApiStrategy {
    
    /**
     * Get the platform name this strategy handles
     * @return Platform identifier (e.g., "JOOM", "AMAZON", "EBAY")
     */
    String getPlatformName();
    
    /**
     * Fetch products from the platform
     * @param credential Platform credentials
     * @param page Page number for pagination
     * @param pageSize Number of items per page
     * @return Map containing product data
     */
    Map<String, Object> fetchProducts(PlatformCredential credential, int page, int pageSize);
    
    /**
     * Fetch a single product by ID
     * @param credential Platform credentials
     * @param productId Platform-specific product ID
     * @return Map containing product data
     * @throws Exception if product not found or API error
     */
    Map<String, Object> fetchProductById(PlatformCredential credential, String productId) throws Exception;
    
    /**
     * Create/upload a product to the platform
     * @param credential Platform credentials
     * @param productData Product data to create
     * @return Map containing created product data with platform ID
     */
    Map<String, Object> createProduct(PlatformCredential credential, Map<String, Object> productData);
    
    /**
     * Update an existing product on the platform
     * @param credential Platform credentials
     * @param productId Platform-specific product ID
     * @param productData Updated product data
     * @return Map containing updated product data
     */
    Map<String, Object> updateProduct(PlatformCredential credential, String productId, Map<String, Object> productData);
    
    /**
     * Delete a product from the platform
     * @param credential Platform credentials
     * @param productId Platform-specific product ID
     * @return true if deletion successful
     */
    boolean deleteProduct(PlatformCredential credential, String productId);
    
    /**
     * Validate platform credentials
     * @param credential Platform credentials to validate
     * @return true if credentials are valid
     */
    boolean validateCredentials(PlatformCredential credential);
}
