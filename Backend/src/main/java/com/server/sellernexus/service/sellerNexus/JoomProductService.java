package com.server.sellernexus.service.sellerNexus;

import com.server.sellernexus.circuitbreaker.CircuitBreaker;
import com.server.sellernexus.circuitbreaker.CircuitBreakerRegistry;
import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.retry.RetryPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JoomProductService {

    private final RestTemplate restTemplate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryPolicy retryPolicy;
    private static final String JOOM_PRODUCTS_API = "https://api-merchant.joom.com/api/v3/products/multi";

    public Map fetchProducts(PlatformCredential credential, int page, int pageSize) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("JOOM-API");
        
        try {
            return circuitBreaker.execute(() -> retryPolicy.execute(() -> {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + credential.getAccessToken());
                HttpEntity<String> entity = new HttpEntity<>(headers);

                // JOOM API uses offset-based pagination
                int offset = (page - 1) * pageSize;
                String url = JOOM_PRODUCTS_API + "?limit=" + pageSize + "&offset=" + offset;

                System.out.println("Fetching JOOM products: page=" + page + ", pageSize=" + pageSize + ", offset=" + offset);
                System.out.println("Using access token (first 20 chars): " + credential.getAccessToken().substring(0, Math.min(20, credential.getAccessToken().length())));
                
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
                Map body = response.getBody();

                System.out.println("JOOM API Response: " + body);

                if (body == null) {
                    System.out.println("WARNING: JOOM API returned null body");
                    return Map.of("items", java.util.Collections.emptyList());
                }

                // Extract data from common container fields
                if (body.containsKey("data")) {
                    Object data = body.get("data");
                    System.out.println("Extracted 'data' field: " + data);
                    
                    // If data is a Map with "items" key, extract the items array directly
                    if (data instanceof Map) {
                        Map dataMap = (Map) data;
                        if (dataMap.containsKey("items")) {
                            Object items = dataMap.get("items");
                            System.out.println("Found nested 'items' in data, extracting: " + (items instanceof java.util.List ? ((java.util.List)items).size() + " items" : items));
                            return Map.of("items", items);
                        }
                    }
                    
                    // Otherwise wrap data itself as items
                    return Map.of("items", data);
                }
                if (body.containsKey("result")) {
                    Object result = body.get("result");
                    System.out.println("Extracted 'result' field: " + result);
                    return Map.of("items", result);
                }

                System.out.println("Returning full body as-is");
                return body;
            }));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map fetchProductById(PlatformCredential credential, String productId) throws Exception {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("JOOM-API");
        
        return circuitBreaker.execute(() -> retryPolicy.execute(() -> {
            String encoded = java.net.URLEncoder.encode(productId, java.nio.charset.StandardCharsets.UTF_8);
            String url = "https://api-merchant.joom.com/api/v3/products?id=" + encoded;
            
            System.out.println("Fetching product from JOOM: " + url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + credential.getAccessToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map body = response.getBody();

            if (body == null) {
                throw new RuntimeException("Empty response from JOOM API");
            }

            return extractProductFromResponse(body);
        }));
    }

    public Map createProduct(PlatformCredential credential, Map<String, Object> productData) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("JOOM-API");
        
        try {
            return circuitBreaker.execute(() -> retryPolicy.execute(() -> {
                String url = "https://api-merchant.joom.com/api/v3/products/create";
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + credential.getAccessToken());
                HttpEntity<Object> entity = new HttpEntity<>(productData, headers);

                System.out.println("Creating product on JOOM with payload: " + productData);
                
                try {
                    ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
                    System.out.println("JOOM Create Response: " + response.getBody());
                    return response.getBody();
                } catch (org.springframework.web.client.HttpClientErrorException ex) {
                    System.err.println("JOOM API Error Response Body: " + ex.getResponseBodyAsString());
                    System.err.println("Status Code: " + ex.getStatusCode());
                    throw new RuntimeException("JOOM API Error: " + ex.getResponseBodyAsString(), ex);
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map extractProductFromResponse(Map response) {
        if (response instanceof Map) {
            Map srcMap = (Map) response;
            
            if (srcMap.containsKey("data") && srcMap.get("data") instanceof Map) {
                return (Map) srcMap.get("data");
            }
            
            if (srcMap.containsKey("result") && srcMap.get("result") instanceof Map) {
                return (Map) srcMap.get("result");
            }
            
            // Response itself might be the product
            return srcMap;
        }
        
        throw new RuntimeException("Unable to extract product from response");
    }

    /**
     * Normalize various image representations from JOOM API into a single URL string.
     * @param imgObj Can be String URL, or object with `origUrl` or `processed` array
     * @return Image URL string or null
     */
    public String extractImageUrl(Object imgObj) {
        try {
            if (imgObj == null) return null;
            
            if (imgObj instanceof String) {
                return (String) imgObj;
            }
            
            if (imgObj instanceof Map) {
                Map m = (Map) imgObj;
                Object orig = m.get("origUrl");
                if (orig instanceof String) return (String) orig;
                
                Object proc = m.get("processed");
                if (proc instanceof java.util.List) {
                    java.util.List l = (java.util.List) proc;
                    // Prefer the last entry (often original) but accept first if only one
                    for (int i = l.size() - 1; i >= 0; i--) {
                        Object it = l.get(i);
                        if (it instanceof Map) {
                            Object url = ((Map) it).get("url");
                            if (url instanceof String) return (String) url;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            // Swallow exception and return null
        }
        
        return null;
    }
}
