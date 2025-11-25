package com.server.sellernexus.service.sellerNexus;

import com.server.sellernexus.exception.ProductNotFoundException;
import com.server.sellernexus.exception.TransferFailedException;
import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.model.sellurNexus.TransferLog;
import com.server.sellernexus.util.SellerNexusTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoomTransferServiceTest {

    @Mock
    private JoomProductService joomProductService;

    @Mock
    private TransferLogService transferLogService;

    @InjectMocks
    private JoomTransferService joomTransferService;

    private PlatformCredential sourceCredential;
    private PlatformCredential targetCredential;
    private TransferLog pendingTransferLog;

    @BeforeEach
    void setUp() {
        sourceCredential = SellerNexusTestDataBuilder.createTestCredential();
        sourceCredential.setId(1L);
        
        targetCredential = SellerNexusTestDataBuilder.createTestCredential();
        targetCredential.setId(2L);
        targetCredential.setAccessToken("target_access_token");
        
        pendingTransferLog = SellerNexusTestDataBuilder.createTestTransferLog();
        pendingTransferLog.setId(100L);
    }

    @Test
    void testTransferProduct_ValidProductWithVariants_Success() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "source_prod_123";
        
        Map<String, Object> sourceProduct = SellerNexusTestDataBuilder.createTestProductWithVariants(
                sourceProductId, "Test Product with Variants");
        
        Map<String, Object> createResponse = SellerNexusTestDataBuilder.createJoomProductResponse(
                SellerNexusTestDataBuilder.createTestProduct("created_prod_456", "Created Product")
        );
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(sourceProduct);
        
        when(joomProductService.createProduct(eq(targetCredential), anyMap()))
                .thenReturn(createResponse);

        // Act
        Map<String, Object> result = joomTransferService.transferProduct(
                sellerId, sourceCredential, targetCredential, sourceProductId);

        // Assert
        assertNotNull(result);
        assertEquals(createResponse, result);
        
        verify(transferLogService).createPending(
                eq(sellerId),
                eq(sourceCredential.getId()),
                eq(targetCredential.getId()),
                eq(sourceProductId),
                eq("JOOM"),
                anyMap()
        );
        
        verify(joomProductService).fetchProductById(sourceCredential, sourceProductId);
        verify(joomProductService).createProduct(eq(targetCredential), anyMap());
        
        verify(transferLogService).markSuccess(
                eq(pendingTransferLog.getId()),
                anyString(),
                eq(createResponse),
                eq("Product transferred successfully")
        );
        
        verify(transferLogService, never()).markFailure(anyLong(), anyString(), anyMap());
    }

    @Test
    void testTransferProduct_ProductWithoutVariants_CreatesFromTopLevel() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "simple_prod_789";
        
        Map<String, Object> sourceProduct = new HashMap<>();
        sourceProduct.put("id", sourceProductId);
        sourceProduct.put("name", "Simple Product");
        sourceProduct.put("sku", "SKU123");
        sourceProduct.put("price", "29.99");
        sourceProduct.put("currency", "USD");
        sourceProduct.put("description", "Simple product without variants");
        
        Map<String, Object> createResponse = SellerNexusTestDataBuilder.createJoomProductResponse(
                SellerNexusTestDataBuilder.createTestProduct("created_simple_999", "Created Simple")
        );
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(sourceProduct);
        
        when(joomProductService.createProduct(eq(targetCredential), anyMap()))
                .thenReturn(createResponse);

        // Act
        Map<String, Object> result = joomTransferService.transferProduct(
                sellerId, sourceCredential, targetCredential, sourceProductId);

        // Assert
        assertNotNull(result);
        
        // Verify createProduct was called with synthesized variant
        verify(joomProductService).createProduct(eq(targetCredential), argThat(payload -> {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> variants = (List<Map<String, Object>>) payload.get("variants");
            
            return variants != null && 
                   variants.size() == 1 && 
                   "SKU123".equals(variants.get(0).get("sku")) &&
                   "29.99".equals(variants.get(0).get("price"));
        }));
    }

    @Test
    void testTransferProduct_SourceProductNotFound_LogsFailure() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "nonexistent_prod";
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomTransferService.transferProduct(
                    sellerId, sourceCredential, targetCredential, sourceProductId);
        });
        
        assertTrue(exception.getMessage().contains("Unable to resolve product object from source response"));
        
        verify(transferLogService).markFailure(
                eq(pendingTransferLog.getId()),
                eq("Unable to resolve product object from source response"),
                anyMap()
        );
        
        verify(joomProductService, never()).createProduct(any(), any());
    }

    @Test
    void testTransferProduct_CreateProductFails_LogsFailure() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "source_prod_abc";
        
        Map<String, Object> sourceProduct = SellerNexusTestDataBuilder.createTestProductWithVariants(
                sourceProductId, "Test Product");
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(sourceProduct);
        
        RuntimeException createException = new RuntimeException("API error: Invalid product data");
        when(joomProductService.createProduct(eq(targetCredential), anyMap()))
                .thenThrow(createException);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomTransferService.transferProduct(
                    sellerId, sourceCredential, targetCredential, sourceProductId);
        });
        
        assertEquals(createException, exception);
        
        verify(transferLogService).markFailure(
                eq(pendingTransferLog.getId()),
                contains("Transfer failed: API error: Invalid product data"),
                anyMap()
        );
        
        verify(transferLogService, never()).markSuccess(anyLong(), anyString(), anyMap(), anyString());
    }

    @Test
    void testTransferProduct_ProductMissingRequiredFields_ThrowsException() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "invalid_prod";
        
        // Product without SKU or price (required for variants)
        Map<String, Object> invalidProduct = new HashMap<>();
        invalidProduct.put("id", sourceProductId);
        invalidProduct.put("name", "Invalid Product");
        // Missing sku, price, and variants
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(invalidProduct);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomTransferService.transferProduct(
                    sellerId, sourceCredential, targetCredential, sourceProductId);
        });
        
        assertTrue(exception.getMessage().contains("missing sku/price") || 
                   exception.getMessage().contains("no variants"));
        
        verify(transferLogService).markFailure(
                eq(pendingTransferLog.getId()),
                anyString(),
                anyMap()
        );
    }

    @Test
    void testMapVariants_VariantMissingPrice_ThrowsException() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "prod_no_price";
        
        Map<String, Object> sourceProduct = new HashMap<>();
        sourceProduct.put("id", sourceProductId);
        sourceProduct.put("name", "Product With Invalid Variant");
        
        List<Map<String, Object>> variants = new ArrayList<>();
        Map<String, Object> variantWithoutPrice = new HashMap<>();
        variantWithoutPrice.put("sku", "VAR123");
        // Missing price field
        variants.add(variantWithoutPrice);
        
        sourceProduct.put("variants", variants);
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(sourceProduct);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomTransferService.transferProduct(
                    sellerId, sourceCredential, targetCredential, sourceProductId);
        });
        
        assertTrue(exception.getMessage().contains("Variant price missing"));
    }

    @Test
    void testMapVariants_GeneratesSkuWhenMissing() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "prod_no_sku";
        
        Map<String, Object> sourceProduct = new HashMap<>();
        sourceProduct.put("id", sourceProductId);
        sourceProduct.put("name", "Product With SKU-less Variant");
        
        List<Map<String, Object>> variants = new ArrayList<>();
        Map<String, Object> variant = new HashMap<>();
        // SKU missing, should be auto-generated
        variant.put("price", "19.99");
        variant.put("currency", "USD");
        variants.add(variant);
        
        sourceProduct.put("variants", variants);
        
        Map<String, Object> createResponse = SellerNexusTestDataBuilder.createJoomProductResponse(
                SellerNexusTestDataBuilder.createTestProduct("created_123", "Created")
        );
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(sourceProduct);
        
        when(joomProductService.createProduct(eq(targetCredential), anyMap()))
                .thenReturn(createResponse);

        // Act
        joomTransferService.transferProduct(
                sellerId, sourceCredential, targetCredential, sourceProductId);

        // Assert - Verify SKU was generated (format: productId-v1)
        verify(joomProductService).createProduct(eq(targetCredential), argThat(payload -> {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mappedVariants = (List<Map<String, Object>>) payload.get("variants");
            
            if (mappedVariants == null || mappedVariants.isEmpty()) return false;
            
            String generatedSku = (String) mappedVariants.get(0).get("sku");
            return generatedSku != null && generatedSku.contains("-v1");
        }));
    }

    @Test
    void testMapVariants_UsesCurrencyFromProductLevel() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "prod_eur";
        
        Map<String, Object> sourceProduct = new HashMap<>();
        sourceProduct.put("id", sourceProductId);
        sourceProduct.put("name", "EUR Product");
        sourceProduct.put("currency", "EUR"); // Product-level currency
        
        List<Map<String, Object>> variants = new ArrayList<>();
        Map<String, Object> variant = new HashMap<>();
        variant.put("sku", "EUR_SKU");
        variant.put("price", "49.99");
        // No variant-level currency, should inherit from product
        variants.add(variant);
        
        sourceProduct.put("variants", variants);
        
        Map<String, Object> createResponse = SellerNexusTestDataBuilder.createJoomProductResponse(
                SellerNexusTestDataBuilder.createTestProduct("created_eur", "Created EUR")
        );
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(sourceProduct);
        
        when(joomProductService.createProduct(eq(targetCredential), anyMap()))
                .thenReturn(createResponse);

        // Act
        joomTransferService.transferProduct(
                sellerId, sourceCredential, targetCredential, sourceProductId);

        // Assert - Verify EUR currency was inherited
        verify(joomProductService).createProduct(eq(targetCredential), argThat(payload -> {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mappedVariants = (List<Map<String, Object>>) payload.get("variants");
            
            return mappedVariants != null && 
                   !mappedVariants.isEmpty() && 
                   "EUR".equals(mappedVariants.get(0).get("currency"));
        }));
    }

    @Test
    void testBuildCreatePayload_CopiesCommonFields() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "full_prod";
        
        Map<String, Object> sourceProduct = new HashMap<>();
        sourceProduct.put("id", sourceProductId);
        sourceProduct.put("name", "Full Product");
        sourceProduct.put("description", "Detailed description");
        sourceProduct.put("brand", "Test Brand");
        sourceProduct.put("categoryId", "CAT123");
        sourceProduct.put("landingPageUrl", "https://example.com/product");
        sourceProduct.put("tags", Arrays.asList("tag1", "tag2"));
        sourceProduct.put("sku", "MAIN_SKU");
        sourceProduct.put("price", "99.99");
        sourceProduct.put("currency", "USD");
        
        Map<String, Object> createResponse = SellerNexusTestDataBuilder.createJoomProductResponse(
                SellerNexusTestDataBuilder.createTestProduct("created_full", "Created Full")
        );
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(sourceProduct);
        
        when(joomProductService.createProduct(eq(targetCredential), anyMap()))
                .thenReturn(createResponse);

        // Act
        joomTransferService.transferProduct(
                sellerId, sourceCredential, targetCredential, sourceProductId);

        // Assert - Verify all common fields were copied
        verify(joomProductService).createProduct(eq(targetCredential), argThat(payload -> {
            return "Full Product".equals(payload.get("name")) &&
                   "Detailed description".equals(payload.get("description")) &&
                   "Test Brand".equals(payload.get("brand")) &&
                   "CAT123".equals(payload.get("categoryId")) &&
                   "https://example.com/product".equals(payload.get("landingPageUrl")) &&
                   payload.get("tags") instanceof List;
        }));
    }

    @Test
    void testExtractCreatedProductId_FromDataField() throws Exception {
        // Arrange
        Integer sellerId = 1;
        String sourceProductId = "source_nested";
        
        Map<String, Object> sourceProduct = SellerNexusTestDataBuilder.createTestProductWithVariants(
                sourceProductId, "Source Product");
        
        Map<String, Object> nestedResponse = new HashMap<>();
        nestedResponse.put("data", Map.of("id", "nested_created_id_789"));
        
        when(transferLogService.createPending(anyInt(), anyLong(), anyLong(), anyString(), anyString(), anyMap()))
                .thenReturn(pendingTransferLog);
        
        when(joomProductService.fetchProductById(eq(sourceCredential), eq(sourceProductId)))
                .thenReturn(sourceProduct);
        
        when(joomProductService.createProduct(eq(targetCredential), anyMap()))
                .thenReturn(nestedResponse);

        // Act
        joomTransferService.transferProduct(
                sellerId, sourceCredential, targetCredential, sourceProductId);

        // Assert - Verify nested ID was extracted and logged
        verify(transferLogService).markSuccess(
                eq(pendingTransferLog.getId()),
                eq("nested_created_id_789"),
                eq(nestedResponse),
                eq("Product transferred successfully")
        );
    }
}
