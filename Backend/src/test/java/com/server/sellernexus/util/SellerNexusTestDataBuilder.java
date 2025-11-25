package com.server.sellernexus.util;

import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.model.sellurNexus.Product;
import com.server.sellernexus.model.sellurNexus.Seller;
import com.server.sellernexus.model.sellurNexus.TransferLog;
import com.server.sellernexus.model.users.User;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Test Data Builder for SellerNexus Unit Tests
 * Provides helper methods to create test objects with default values
 */
public class SellerNexusTestDataBuilder {

    // User builders
    public static User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setEmailVerified(true);
        return user;
    }

    public static User createTestUser(Integer id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setEmailVerified(true);
        return user;
    }

    // Seller builders
    public static Seller createTestSeller() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("Test Seller");
        seller.setEmail("seller@example.com");
        seller.setCreatedAt(LocalDateTime.now());
        return seller;
    }

    public static Seller createTestSeller(Long id, String name, String email) {
        Seller seller = new Seller();
        seller.setId(id);
        seller.setName(name);
        seller.setEmail(email);
        seller.setCreatedAt(LocalDateTime.now());
        return seller;
    }

    // PlatformCredential builders
    public static PlatformCredential createTestCredential() {
        return PlatformCredential.builder()
                .id(1L)
                .platform("JOOM")
                .seller(createTestUser())
                .accessToken("test_access_token_12345")
                .refreshToken("test_refresh_token_67890")
                .expiresIn(3600)
                .expiryTime(System.currentTimeMillis() + 3600000)
                .externalMerchantId("merchant123")
                .apiKey("client_id_123")
                .apiSecret("client_secret_456")
                .build();
    }

    public static PlatformCredential createTestCredential(Long id, User user, String platform) {
        return PlatformCredential.builder()
                .id(id)
                .platform(platform)
                .seller(user)
                .accessToken("test_access_token_" + id)
                .refreshToken("test_refresh_token_" + id)
                .expiresIn(3600)
                .expiryTime(System.currentTimeMillis() + 3600000)
                .externalMerchantId("merchant" + id)
                .apiKey("client_id_" + id)
                .apiSecret("client_secret_" + id)
                .build();
    }

    public static PlatformCredential createExpiredCredential() {
        return PlatformCredential.builder()
                .id(1L)
                .platform("JOOM")
                .seller(createTestUser())
                .accessToken("expired_access_token")
                .refreshToken("test_refresh_token")
                .expiresIn(3600)
                .expiryTime(System.currentTimeMillis() - 1000) // Expired 1 second ago
                .externalMerchantId("merchant123")
                .build();
    }

    // Product builders
    public static Product createTestProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setProductName("Test Product");
        product.setSku("SKU-TEST-001");
        product.setPrice(new java.math.BigDecimal("99.99"));
        product.setQuantity(10);
        product.setSeller(createTestSeller());
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }

    public static Map<String, Object> createTestProductMap() {
        Map<String, Object> product = new HashMap<>();
        product.put("id", "prod_123");
        product.put("name", "Test Product");
        product.put("description", "Test description");
        product.put("sku", "SKU-001");
        product.put("price", "99.99");
        product.put("currency", "USD");
        product.put("mainImage", "https://example.com/image.jpg");
        product.put("brand", "Test Brand");
        product.put("categoryId", "cat_001");
        return product;
    }

    public static Map<String, Object> createTestProduct(String id, String name) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", id);
        product.put("name", name);
        product.put("description", "Test description for " + name);
        product.put("sku", "SKU-" + id);
        product.put("price", "99.99");
        product.put("currency", "USD");
        product.put("mainImage", "https://example.com/image.jpg");
        product.put("brand", "Test Brand");
        product.put("categoryId", "cat_001");
        return product;
    }

    public static Map<String, Object> createTestProductWithVariants() {
        Map<String, Object> product = createTestProductMap();
        
        java.util.List<Map<String, Object>> variants = new java.util.ArrayList<>();
        
        Map<String, Object> variant1 = new HashMap<>();
        variant1.put("sku", "SKU-001-RED");
        variant1.put("price", "99.99");
        variant1.put("currency", "USD");
        variant1.put("inventory", 10);
        variants.add(variant1);
        
        Map<String, Object> variant2 = new HashMap<>();
        variant2.put("sku", "SKU-001-BLUE");
        variant2.put("price", "109.99");
        variant2.put("currency", "USD");
        variant2.put("inventory", 5);
        variants.add(variant2);
        
        product.put("variants", variants);
        return product;
    }

    public static Map<String, Object> createTestProductWithVariants(String id, String name) {
        Map<String, Object> product = createTestProduct(id, name);
        
        java.util.List<Map<String, Object>> variants = new java.util.ArrayList<>();
        
        Map<String, Object> variant1 = new HashMap<>();
        variant1.put("sku", "SKU-" + id + "-RED");
        variant1.put("price", "99.99");
        variant1.put("currency", "USD");
        variant1.put("inventory", 10);
        variants.add(variant1);
        
        Map<String, Object> variant2 = new HashMap<>();
        variant2.put("sku", "SKU-" + id + "-BLUE");
        variant2.put("price", "109.99");
        variant2.put("currency", "USD");
        variant2.put("inventory", 5);
        variants.add(variant2);
        
        product.put("variants", variants);
        return product;
    }

    // TransferLog builders
    public static TransferLog createTestTransferLog() {
        TransferLog log = new TransferLog();
        log.setId(1L);
        log.setSellerId(1);
        log.setSourceCredentialId(1L);
        log.setTargetCredentialId(2L);
        log.setSourceProductExtId("source_prod_123");
        log.setPlatformName("JOOM");
        log.setStatus("PENDING");
        log.setStartedAt(new Timestamp(System.currentTimeMillis()));
        return log;
    }

    public static TransferLog createSuccessfulTransferLog() {
        TransferLog log = createTestTransferLog();
        log.setStatus("SUCCESS");
        log.setTargetProductExtId("target_prod_456");
        log.setMessage("Transfer completed successfully");
        log.setFinishedAt(new Timestamp(System.currentTimeMillis() + 5000));
        log.setDurationMs(5000);
        return log;
    }

    public static TransferLog createFailedTransferLog() {
        TransferLog log = createTestTransferLog();
        log.setStatus("FAILED");
        log.setErrorMessage("Product not found");
        log.setMessage("Transfer failed: Product not found");
        log.setFinishedAt(new Timestamp(System.currentTimeMillis() + 2000));
        log.setDurationMs(2000);
        return log;
    }

    // OAuth response builders
    public static Map<String, Object> createJoomTokenResponse() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        data.put("access_token", "new_access_token_12345");
        data.put("refresh_token", "new_refresh_token_67890");
        data.put("expires_in", 3600);
        data.put("expiry_time", (int) (System.currentTimeMillis() / 1000 + 3600));
        data.put("merchant_user_id", "merchant_123");
        
        response.put("data", data);
        response.put("status", "success");
        
        return response;
    }

    public static Map<String, Object> createJoomTokenResponse(String accessToken, String refreshToken, 
                                                                int expiresIn, long expiryTime, String merchantId) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        data.put("access_token", accessToken);
        data.put("refresh_token", refreshToken);
        data.put("expires_in", expiresIn);
        data.put("expiry_time", (int) (expiryTime / 1000));
        data.put("merchant_user_id", merchantId);
        
        response.put("data", data);
        response.put("status", "success");
        
        return response;
    }

    public static Map<String, Object> createJoomProductResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("data", createTestProductMap());
        response.put("status", "success");
        return response;
    }

    public static Map<String, Object> createJoomProductResponse(Map<String, Object> productData) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", productData);
        response.put("status", "success");
        return response;
    }

    public static Map<String, Object> createJoomProductsListResponse() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        java.util.List<Map<String, Object>> items = new java.util.ArrayList<>();
        items.add(createTestProductMap());
        items.add(createTestProductMap());
        
        data.put("items", items);
        response.put("data", data);
        response.put("status", "success");
        
        return response;
    }

    public static Map<String, Object> createJoomProductListResponse(java.util.List<Map<String, Object>> products) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        data.put("items", products);
        response.put("data", data);
        response.put("status", "success");
        
        return response;
    }

    // State parameter builder for OAuth
    public static String createTestStateParameter(Integer userId, String clientId, String label) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", userId);
        payload.put("cid", clientId);
        if (label != null) {
            payload.put("label", label);
        }
        payload.put("ts", System.currentTimeMillis());
        
        // For testing, just create a simple encoded string
        String json = String.format("{\"uid\":%d,\"cid\":\"%s\",\"label\":\"%s\",\"ts\":%d}", 
            userId, clientId, label, System.currentTimeMillis());
        String encoded = java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        
        return encoded + ".test_signature";
    }
}
