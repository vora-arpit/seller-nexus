package com.server.sellernexus.service.sellerNexus;

import com.server.sellernexus.exception.ProductNotFoundException;
import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.util.SellerNexusTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoomProductServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private JoomProductService joomProductService;

    private PlatformCredential testCredential;

    @BeforeEach
    void setUp() {
        testCredential = SellerNexusTestDataBuilder.createTestCredential();
    }

    @Test
    void testFetchProducts_ValidCredentials_ReturnsProducts() {
        // Arrange
        int page = 1;
        int pageSize = 10;
        
        List<Map<String, Object>> mockProducts = Arrays.asList(
                SellerNexusTestDataBuilder.createTestProduct("product_1", "Product One"),
                SellerNexusTestDataBuilder.createTestProduct("product_2", "Product Two")
        );
        
        Map<String, Object> mockResponse = SellerNexusTestDataBuilder.createJoomProductListResponse(mockProducts);
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        Map result = joomProductService.fetchProducts(testCredential, page, pageSize);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("items"));
        
        Object items = result.get("items");
        assertTrue(items instanceof List);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> productList = (List<Map<String, Object>>) items;
        assertEquals(2, productList.size());
        assertEquals("product_1", productList.get(0).get("id"));
        assertEquals("Product One", productList.get(0).get("name"));
        
        verify(restTemplate, times(1)).exchange(
                contains("https://api-merchant.joom.com/api/v3/products/multi"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    void testFetchProducts_SecondPage_CalculatesCorrectOffset() {
        // Arrange
        int page = 2;
        int pageSize = 20;
        int expectedOffset = 20; // (page-1) * pageSize = (2-1) * 20
        
        Map<String, Object> mockResponse = Map.of("data", Map.of("items", Collections.emptyList()));
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        joomProductService.fetchProducts(testCredential, page, pageSize);

        // Assert
        verify(restTemplate).exchange(
                contains("limit=20&offset=20"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    void testFetchProducts_NullResponse_ReturnsEmptyList() {
        // Arrange
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        Map result = joomProductService.fetchProducts(testCredential, 1, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("items"));
        
        @SuppressWarnings("unchecked")
        List<Object> items = (List<Object>) result.get("items");
        assertTrue(items.isEmpty());
    }

    @Test
    void testFetchProducts_ResponseWithResultField_ExtractsCorrectly() {
        // Arrange
        List<Map<String, Object>> products = Collections.singletonList(
                SellerNexusTestDataBuilder.createTestProduct("prod_123", "Test Product")
        );
        
        Map<String, Object> mockResponse = Map.of("result", products);
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        Map result = joomProductService.fetchProducts(testCredential, 1, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("items"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
        assertEquals(1, items.size());
        assertEquals("prod_123", items.get(0).get("id"));
    }

    @Test
    void testFetchProductById_ValidId_ReturnsProduct() throws Exception {
        // Arrange
        String productId = "test_product_123";
        Map<String, Object> productData = SellerNexusTestDataBuilder.createTestProduct(productId, "Test Product");
        Map<String, Object> mockResponse = SellerNexusTestDataBuilder.createJoomProductResponse(productData);
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        Map result = joomProductService.fetchProductById(testCredential, productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.get("id"));
        assertEquals("Test Product", result.get("name"));
        
        verify(restTemplate).exchange(
                contains("https://api-merchant.joom.com/api/v3/products?id="),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    void testFetchProductById_ProductIdWithSpecialCharacters_EncodesCorrectly() throws Exception {
        // Arrange
        String productId = "product id with spaces & special=chars";
        Map<String, Object> mockResponse = SellerNexusTestDataBuilder.createJoomProductResponse(
                SellerNexusTestDataBuilder.createTestProduct(productId, "Special Product")
        );
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        joomProductService.fetchProductById(testCredential, productId);

        // Assert - Verify URL encoding happened (spaces become +, & becomes %26, etc.)
        verify(restTemplate).exchange(
                contains("product+id+with+spaces+%26+special%3Dchars"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    void testFetchProductById_NullResponse_ThrowsException() {
        // Arrange
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            joomProductService.fetchProductById(testCredential, "product_123");
        });
        
        assertTrue(exception.getMessage().contains("Empty response from JOOM API"));
    }

    @Test
    void testCreateProduct_ValidData_ReturnsCreatedProduct() {
        // Arrange
        Map<String, Object> productData = SellerNexusTestDataBuilder.createTestProduct("new_prod", "New Product");
        Map<String, Object> mockResponse = SellerNexusTestDataBuilder.createJoomProductResponse(productData);
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act
        Map result = joomProductService.createProduct(testCredential, productData);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
        
        verify(restTemplate).postForEntity(
                eq("https://api-merchant.joom.com/api/v3/products/create"),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    void testCreateProduct_ApiError_ThrowsRuntimeException() {
        // Arrange
        Map<String, Object> productData = SellerNexusTestDataBuilder.createTestProduct("bad_prod", "Bad Product");
        String errorResponse = "{\"error\":\"Invalid product data\",\"code\":400}";
        
        HttpClientErrorException apiException = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                errorResponse.getBytes(),
                null
        );
        
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(apiException);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomProductService.createProduct(testCredential, productData);
        });
        
        assertTrue(exception.getMessage().contains("JOOM API Error"));
        assertTrue(exception.getCause() instanceof HttpClientErrorException);
    }

    @Test
    void testExtractImageUrl_StringUrl_ReturnsUrl() {
        // Arrange
        String imageUrl = "https://example.com/image.jpg";

        // Act
        String result = joomProductService.extractImageUrl(imageUrl);

        // Assert
        assertEquals(imageUrl, result);
    }

    @Test
    void testExtractImageUrl_MapWithOrigUrl_ReturnsOrigUrl() {
        // Arrange
        Map<String, Object> imageObject = Map.of("origUrl", "https://example.com/original.jpg");

        // Act
        String result = joomProductService.extractImageUrl(imageObject);

        // Assert
        assertEquals("https://example.com/original.jpg", result);
    }

    @Test
    void testExtractImageUrl_MapWithProcessedArray_ReturnsLastUrl() {
        // Arrange
        List<Map<String, Object>> processedImages = Arrays.asList(
                Map.of("url", "https://example.com/thumbnail.jpg", "size", "small"),
                Map.of("url", "https://example.com/medium.jpg", "size", "medium"),
                Map.of("url", "https://example.com/large.jpg", "size", "large")
        );
        
        Map<String, Object> imageObject = Map.of("processed", processedImages);

        // Act
        String result = joomProductService.extractImageUrl(imageObject);

        // Assert
        assertEquals("https://example.com/large.jpg", result);
    }

    @Test
    void testExtractImageUrl_MapWithProcessedArraySingleItem_ReturnsUrl() {
        // Arrange
        List<Map<String, Object>> processedImages = Collections.singletonList(
                Map.of("url", "https://example.com/single.jpg")
        );
        
        Map<String, Object> imageObject = Map.of("processed", processedImages);

        // Act
        String result = joomProductService.extractImageUrl(imageObject);

        // Assert
        assertEquals("https://example.com/single.jpg", result);
    }

    @Test
    void testExtractImageUrl_NullInput_ReturnsNull() {
        // Act
        String result = joomProductService.extractImageUrl(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testExtractImageUrl_EmptyMap_ReturnsNull() {
        // Arrange
        Map<String, Object> emptyMap = Collections.emptyMap();

        // Act
        String result = joomProductService.extractImageUrl(emptyMap);

        // Assert
        assertNull(result);
    }

    @Test
    void testExtractImageUrl_InvalidObject_ReturnsNull() {
        // Arrange
        Integer invalidObject = 12345;

        // Act
        String result = joomProductService.extractImageUrl(invalidObject);

        // Assert
        assertNull(result);
    }

    @Test
    void testExtractImageUrl_MapWithProcessedEmptyArray_ReturnsNull() {
        // Arrange
        Map<String, Object> imageObject = Map.of("processed", Collections.emptyList());

        // Act
        String result = joomProductService.extractImageUrl(imageObject);

        // Assert
        assertNull(result);
    }
}
