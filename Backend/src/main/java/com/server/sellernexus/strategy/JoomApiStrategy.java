package com.server.sellernexus.strategy;

import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * ============================================
 * STRATEGY DESIGN PATTERN - Concrete Strategy
 * ============================================
 * 
 * JoomApiStrategy - Joom platform-specific implementation
 * 
 * Purpose: Implements PlatformApiStrategy for Joom marketplace
 * Benefits:
 * - Encapsulates all Joom-specific API logic
 * - Can be swapped with other platform strategies
 * - Easy to maintain and test independently
 * 
 * Pattern Type: Strategy Pattern - Concrete Strategy
 * 
 * This class contains all the Joom-specific API implementation details
 * including authentication, endpoint URLs, request/response formatting
 */
@Component
@RequiredArgsConstructor
public class JoomApiStrategy implements PlatformApiStrategy {
    
    private final RestTemplate restTemplate;
    private static final String JOOM_API_BASE = "https://api-merchant.joom.com/api/v2";
    
    @Override
    public String getPlatformName() {
        return "JOOM";
    }
    
    @Override
    public Map<String, Object> fetchProducts(PlatformCredential credential, int page, int pageSize) {
        String url = String.format("%s/products?page=%d&limit=%d", JOOM_API_BASE, page, pageSize);
        
        HttpHeaders headers = createHeaders(credential);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }
    
    @Override
    public Map<String, Object> fetchProductById(PlatformCredential credential, String productId) throws Exception {
        String url = String.format("%s/products/%s", JOOM_API_BASE, productId);
        
        HttpHeaders headers = createHeaders(credential);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }
    
    @Override
    public Map<String, Object> createProduct(PlatformCredential credential, Map<String, Object> productData) {
        String url = JOOM_API_BASE + "/products";
        
        HttpHeaders headers = createHeaders(credential);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(productData, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        return response.getBody();
    }
    
    @Override
    public Map<String, Object> updateProduct(PlatformCredential credential, String productId, Map<String, Object> productData) {
        String url = String.format("%s/products/%s", JOOM_API_BASE, productId);
        
        HttpHeaders headers = createHeaders(credential);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(productData, headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
        return response.getBody();
    }
    
    @Override
    public boolean deleteProduct(PlatformCredential credential, String productId) {
        try {
            String url = String.format("%s/products/%s", JOOM_API_BASE, productId);
            
            HttpHeaders headers = createHeaders(credential);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean validateCredentials(PlatformCredential credential) {
        try {
            String url = JOOM_API_BASE + "/merchant/info";
            
            HttpHeaders headers = createHeaders(credential);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Create HTTP headers with Joom authentication
     */
    private HttpHeaders createHeaders(PlatformCredential credential) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + credential.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
