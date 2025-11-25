package com.server.crm1.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

/**
 * ============================================
 * SINGLETON DESIGN PATTERN IMPLEMENTATION
 * ============================================
 * 
 * ApiClientConfigManager - Thread-Safe Singleton Pattern
 * 
 * Purpose: Centralized configuration manager for external API clients
 * Benefits:
 * - Single point of configuration for all API integrations
 * - Efficient resource management (single RestTemplate, ObjectMapper instance)
 * - Thread-safe implementation using Bill Pugh Singleton Design
 * - Lazy initialization - created only when needed
 * 
 * Pattern Type: Bill Pugh Singleton (Thread-Safe, Lazy Initialization)
 * 
 * Usage Example:
 * ApiClientConfigManager manager = ApiClientConfigManager.getInstance();
 * RestTemplate restTemplate = manager.getRestTemplate();
 * ObjectMapper mapper = manager.getObjectMapper();
 */
public class ApiClientConfigManager {

    // Private constructor prevents instantiation from outside
    private ApiClientConfigManager() {
        // Initialize configuration
        initializeConfiguration();
    }

    /**
     * SingletonHolder - Inner static class for lazy initialization
     * This approach is thread-safe without requiring synchronization
     * The instance is created only when getInstance() is called for the first time
     */
    private static class SingletonHolder {
        private static final ApiClientConfigManager INSTANCE = new ApiClientConfigManager();
    }

    /**
     * Public method to get the singleton instance
     * Thread-safe and doesn't require explicit synchronization
     */
    public static ApiClientConfigManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // Configuration objects
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    
    // API Configuration settings
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    /**
     * Initialize all configuration objects
     */
    private void initializeConfiguration() {
        this.restTemplate = createRestTemplate();
        this.objectMapper = createObjectMapper();
    }

    /**
     * Create and configure RestTemplate instance
     */
    private RestTemplate createRestTemplate() {
        RestTemplate template = new RestTemplate();
        // Additional configuration can be added here
        // For example: timeout settings, error handlers, interceptors
        return template;
    }

    /**
     * Create and configure ObjectMapper instance
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Additional configuration can be added here
        // For example: date formats, null handling, custom serializers
        return mapper;
    }

    /**
     * Get the shared RestTemplate instance
     */
    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }

    /**
     * Get the shared ObjectMapper instance
     */
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    /**
     * Get connection timeout configuration
     */
    public int getConnectionTimeout() {
        return CONNECTION_TIMEOUT;
    }

    /**
     * Get read timeout configuration
     */
    public int getReadTimeout() {
        return READ_TIMEOUT;
    }

    /**
     * Prevent cloning of singleton instance
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Singleton instance cannot be cloned");
    }

    /**
     * Configuration for specific API platforms
     */
    public static class JoomApiConfig {
        public static final String BASE_URL = "https://api-merchant.joom.com/api/v2";
        public static final String AUTH_URL = BASE_URL + "/oauth/authorize";
        public static final String TOKEN_URL = BASE_URL + "/oauth/access_token";
        public static final String REFRESH_URL = BASE_URL + "/oauth/refresh_token";
        public static final String AUTH_TEST_URL = BASE_URL + "/auth_test";
    }

    /**
     * Get Joom API configuration
     */
    public JoomApiConfig getJoomApiConfig() {
        return new JoomApiConfig();
    }
}
