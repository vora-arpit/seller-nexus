package com.server.sellernexus.service.sellerNexus;

import com.server.sellernexus.exception.InvalidCredentialException;
import com.server.sellernexus.exception.TokenExpiredException;
import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.model.users.User;
import com.server.sellernexus.repository.sellerNexus.PlatformCredentialRepository;
import com.server.sellernexus.repository.sellerNexus.SellerRepository;
import com.server.sellernexus.service.UserService;
import com.server.sellernexus.util.SellerNexusTestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoomAuthServiceTest {

    @Mock
    private PlatformCredentialRepository credentialRepo;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SellerRepository sellerRepo;

    @Mock
    private UserService userService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private JoomAuthService joomAuthService;

    private static final String TEST_TOKEN_SECRET = "test_secret_key_for_hmac_signing_operations";
    private static final String TEST_CLIENT_ID = "test_client_id_123";
    private static final String TEST_CLIENT_SECRET = "test_client_secret_456";
    private static final String REDIRECT_URI = "http://localhost:8080/api/joom/auth/callback";

    private User testUser;
    private PlatformCredential testCredential;

    @BeforeEach
    void setUp() {
        // Inject token secret for HMAC signing
        ReflectionTestUtils.setField(joomAuthService, "tokenSecret", TEST_TOKEN_SECRET);
        
        // Create test data
        testUser = SellerNexusTestDataBuilder.createTestUser();
        testCredential = SellerNexusTestDataBuilder.createTestCredential();
    }

    @Test
    void testGetAuthorizationUrl_ValidParams_ReturnsUrlWithSignedState() {
        // Arrange
        Integer userId = 1;
        String label = "test_label";

        // Act
        String authUrl = joomAuthService.getAuthorizationUrl(TEST_CLIENT_ID, userId, label);

        // Assert
        assertNotNull(authUrl);
        assertTrue(authUrl.startsWith("https://api-merchant.joom.com/api/v2/oauth/authorize"));
        assertTrue(authUrl.contains("client_id=" + TEST_CLIENT_ID));
        assertTrue(authUrl.contains("redirect_uri=" + REDIRECT_URI));
        assertTrue(authUrl.contains("response_type=code"));
        assertTrue(authUrl.contains("prompt=login"));
        assertTrue(authUrl.contains("state="));
        
        // Extract and verify state parameter structure (should be base64.signature)
        String stateParam = extractStateFromUrl(authUrl);
        assertNotNull(stateParam);
        assertTrue(stateParam.contains("."), "State should contain signature separator");
        String[] stateParts = stateParam.split("\\.");
        assertEquals(2, stateParts.length, "State should have payload and signature");
    }

    @Test
    void testGetAuthorizationUrl_NullLabel_ReturnsValidUrl() {
        // Arrange
        Integer userId = 1;

        // Act
        String authUrl = joomAuthService.getAuthorizationUrl(TEST_CLIENT_ID, userId, null);

        // Assert
        assertNotNull(authUrl);
        assertTrue(authUrl.contains("client_id=" + TEST_CLIENT_ID));
        assertTrue(authUrl.contains("state="));
    }

    @Test
    void testParseState_ValidSignedState_ReturnsPayload() throws Exception {
        // Arrange
        String state = joomAuthService.getAuthorizationUrl(TEST_CLIENT_ID, 1, "test_label")
                .split("state=")[1];

        // Act
        Map<String, Object> payload = joomAuthService.parseState(state);

        // Assert
        assertNotNull(payload);
        assertEquals(1, payload.get("uid"));
        assertEquals(TEST_CLIENT_ID, payload.get("cid"));
        assertEquals("test_label", payload.get("label"));
        assertTrue(payload.containsKey("ts"));
    }

    @Test
    void testParseState_InvalidState_ThrowsException() {
        // Arrange
        String invalidState = "invalid_state_without_signature";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomAuthService.parseState(invalidState);
        });
        
        assertTrue(exception.getMessage().contains("Invalid state"));
    }

    @Test
    void testParseState_TamperedState_ThrowsException() throws Exception {
        // Arrange - Create valid state then tamper with payload
        String validState = joomAuthService.getAuthorizationUrl(TEST_CLIENT_ID, 1, "original")
                .split("state=")[1];
        
        String[] parts = validState.split("\\.");
        String tamperedPayload = parts[0] + "TAMPERED";
        String tamperedState = tamperedPayload + "." + parts[1];

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomAuthService.parseState(tamperedState);
        });
        
        assertTrue(exception.getMessage().contains("Invalid state signature") || 
                   exception.getMessage().contains("Failed to parse state"));
    }

    @Test
    void testParseState_NullState_ThrowsException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomAuthService.parseState(null);
        });
        
        assertTrue(exception.getMessage().contains("Invalid state"));
    }

    @Test
    void testStoreAndRetrieveSecret_ValidState_Success() {
        // Arrange
        String state = "test_state_123";
        String secret = "test_secret_456";

        // Act
        joomAuthService.storeSecretForState(state, secret);
        String retrievedSecret = joomAuthService.retrieveAndRemoveSecretForState(state);

        // Assert
        assertEquals(secret, retrievedSecret);
        
        // Verify secret is removed after retrieval
        String secondRetrieval = joomAuthService.retrieveAndRemoveSecretForState(state);
        assertNull(secondRetrieval);
    }

    @Test
    void testRetrieveSecret_NonExistentState_ReturnsNull() {
        // Act
        String result = joomAuthService.retrieveAndRemoveSecretForState("non_existent_state");

        // Assert
        assertNull(result);
    }

    @Test
    void testStoreSecret_NullParameters_HandlesGracefully() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            joomAuthService.storeSecretForState(null, "secret");
            joomAuthService.storeSecretForState("state", null);
            joomAuthService.storeSecretForState(null, null);
        });
    }

    @Test
    void testExchangeCodeForToken_ValidCode_ReturnsCredential() {
        // Arrange
        String authCode = "test_auth_code_xyz";
        Map<String, Object> mockResponse = SellerNexusTestDataBuilder.createJoomTokenResponse(
                "access_token_12345",
                "refresh_token_67890",
                3600,
                System.currentTimeMillis() + 3600000,
                "merchant123"
        );
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(responseEntity);
        
        when(credentialRepo.save(any(PlatformCredential.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PlatformCredential result = joomAuthService.exchangeCodeForToken(
                testUser, TEST_CLIENT_ID, TEST_CLIENT_SECRET, authCode);

        // Assert
        assertNotNull(result);
        assertEquals("JOOM", result.getPlatform());
        assertEquals("access_token_12345", result.getAccessToken());
        assertEquals("refresh_token_67890", result.getRefreshToken());
        assertEquals(3600, result.getExpiresIn());
        assertEquals("merchant123", result.getExternalMerchantId());
        assertEquals(TEST_CLIENT_ID, result.getApiKey());
        assertEquals(TEST_CLIENT_SECRET, result.getApiSecret());
        assertEquals(testUser, result.getSeller());
        
        verify(credentialRepo, times(1)).save(any(PlatformCredential.class));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(Map.class));
    }

    @Test
    void testExchangeCodeForToken_InvalidCode_ThrowsException() {
        // Arrange
        String invalidCode = "invalid_code";
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomAuthService.exchangeCodeForToken(
                    testUser, TEST_CLIENT_ID, TEST_CLIENT_SECRET, invalidCode);
        });
        
        assertTrue(exception.getMessage().contains("Invalid token response"));
    }

    @Test
    void testExchangeCodeForToken_MissingDataField_ThrowsException() {
        // Arrange
        String authCode = "test_auth_code";
        Map<String, Object> mockResponse = Map.of("error", "invalid_grant");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomAuthService.exchangeCodeForToken(
                    testUser, TEST_CLIENT_ID, TEST_CLIENT_SECRET, authCode);
        });
        
        assertTrue(exception.getMessage().contains("'data' field is missing"));
    }

    @Test
    void testRefreshAccessToken_ValidToken_ReturnsUpdatedCredential() {
        // Arrange
        PlatformCredential expiredCred = SellerNexusTestDataBuilder.createExpiredCredential();
        
        long newExpiryTime = System.currentTimeMillis() + 7200000;
        Map<String, Object> mockResponse = SellerNexusTestDataBuilder.createJoomTokenResponse(
                "new_access_token_abc",
                "new_refresh_token_def",
                7200,
                newExpiryTime,
                "merchant123"
        );
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenReturn(responseEntity);
        
        when(credentialRepo.save(any(PlatformCredential.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PlatformCredential result = joomAuthService.refreshAccessToken(expiredCred);

        // Assert
        assertNotNull(result);
        assertEquals("new_access_token_abc", result.getAccessToken());
        assertEquals("new_refresh_token_def", result.getRefreshToken());
        assertEquals(7200, result.getExpiresIn());
        assertEquals(newExpiryTime, result.getExpiryTime());
        
        verify(credentialRepo, times(1)).save(any(PlatformCredential.class));
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(Map.class));
    }

    @Test
    void testRefreshAccessToken_InvalidRefreshToken_ThrowsException() {
        // Arrange
        PlatformCredential invalidCred = SellerNexusTestDataBuilder.createTestCredential();
        invalidCred.setRefreshToken("invalid_refresh_token");
        
        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("Invalid refresh token"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            joomAuthService.refreshAccessToken(invalidCred);
        });
        
        assertTrue(exception.getMessage().contains("Invalid refresh token"));
        verify(credentialRepo, never()).save(any());
    }

    @Test
    void testTestAccessToken_ValidToken_CallsApiSuccessfully() {
        // Arrange
        String accessToken = "valid_access_token";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(
                "{\"status\":\"success\"}", HttpStatus.OK);
        
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            joomAuthService.testAccessToken(accessToken);
        });
        
        verify(restTemplate, times(1)).exchange(
                eq("https://api-merchant.joom.com/api/v2/auth_test"),
                any(), any(), eq(String.class));
    }

    // Helper method to extract state parameter from URL
    private String extractStateFromUrl(String url) {
        String[] parts = url.split("state=");
        if (parts.length < 2) return null;
        return parts[1].split("&")[0]; // Get state value before next parameter
    }
}
