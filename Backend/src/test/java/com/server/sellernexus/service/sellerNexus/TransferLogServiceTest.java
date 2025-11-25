package com.server.sellernexus.service.sellerNexus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.sellernexus.model.sellurNexus.TransferLog;
import com.server.sellernexus.repository.sellerNexus.TransferLogRepository;
import com.server.sellernexus.util.SellerNexusTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferLogServiceTest {

    @Mock
    private TransferLogRepository repository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private TransferLogService transferLogService;

    private TransferLog testTransferLog;

    @BeforeEach
    void setUp() {
        testTransferLog = SellerNexusTestDataBuilder.createTestTransferLog();
        testTransferLog.setId(1L);
    }

    @Test
    void testCreatePending_ValidData_CreatesLog() {
        // Arrange
        Integer sellerId = 1;
        Long sourceCredentialId = 10L;
        Long targetCredentialId = 20L;
        String sourceProductExtId = "source_product_123";
        String platformName = "JOOM";
        Map<String, Object> requestPayload = Map.of(
                "sourceProductId", sourceProductExtId,
                "targetPlatform", platformName
        );
        
        when(repository.save(any(TransferLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransferLog result = transferLogService.createPending(
                sellerId, sourceCredentialId, targetCredentialId,
                sourceProductExtId, platformName, requestPayload);

        // Assert
        assertNotNull(result);
        assertEquals(sellerId, result.getSellerId());
        assertEquals(sourceCredentialId, result.getSourceCredentialId());
        assertEquals(targetCredentialId, result.getTargetCredentialId());
        assertEquals(sourceProductExtId, result.getSourceProductExtId());
        assertEquals(platformName, result.getPlatformName());
        assertEquals("PENDING", result.getStatus());
        assertNotNull(result.getStartedAt());
        assertNotNull(result.getRequestPayload());
        assertTrue(result.getRequestPayload().contains("source_product_123"));
        
        verify(repository, times(1)).save(any(TransferLog.class));
    }

    @Test
    void testCreatePending_MasksAccessToken() {
        // Arrange
        Integer sellerId = 1;
        Map<String, Object> requestPayload = Map.of(
                "accessToken", "secret_token_12345",
                "productId", "prod_123"
        );
        
        when(repository.save(any(TransferLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransferLog result = transferLogService.createPending(
                sellerId, 1L, 2L, "prod_123", "JOOM", requestPayload);

        // Assert
        assertNotNull(result.getRequestPayload());
        assertFalse(result.getRequestPayload().contains("secret_token_12345"),
                "Access token should be masked");
        assertTrue(result.getRequestPayload().contains("***MASKED***"),
                "Should contain mask placeholder");
    }

    @Test
    void testMarkSuccess_ValidLog_UpdatesSuccessfully() {
        // Arrange
        Long logId = 1L;
        String targetProductExtId = "target_product_456";
        String successMessage = "Transfer completed successfully";
        Map<String, Object> responsePayload = Map.of(
                "id", targetProductExtId,
                "status", "created"
        );
        
        TransferLog pendingLog = SellerNexusTestDataBuilder.createTestTransferLog();
        pendingLog.setId(logId);
        pendingLog.setStatus("PENDING");
        pendingLog.setStartedAt(new Timestamp(System.currentTimeMillis() - 5000));
        
        when(repository.findById(logId)).thenReturn(Optional.of(pendingLog));
        when(repository.save(any(TransferLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransferLog result = transferLogService.markSuccess(
                logId, targetProductExtId, responsePayload, successMessage);

        // Assert
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(targetProductExtId, result.getTargetProductExtId());
        assertEquals(successMessage, result.getMessage());
        assertNotNull(result.getResponsePayload());
        assertTrue(result.getResponsePayload().contains(targetProductExtId));
        assertNotNull(result.getFinishedAt());
        assertNotNull(result.getDurationMs());
        assertTrue(result.getDurationMs() > 0);
        
        verify(repository).findById(logId);
        verify(repository).save(any(TransferLog.class));
    }

    @Test
    void testMarkSuccess_LogNotFound_ThrowsException() {
        // Arrange
        Long logId = 999L;
        when(repository.findById(logId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transferLogService.markSuccess(
                    logId, "target_123", Map.of(), "Success");
        });
        
        assertTrue(exception.getMessage().contains("TransferLog not found"));
        verify(repository, never()).save(any());
    }

    @Test
    void testMarkSuccess_NullMessage_UsesDefaultMessage() {
        // Arrange
        Long logId = 1L;
        TransferLog pendingLog = SellerNexusTestDataBuilder.createTestTransferLog();
        pendingLog.setId(logId);
        pendingLog.setStatus("PENDING");
        pendingLog.setStartedAt(new Timestamp(System.currentTimeMillis()));
        
        when(repository.findById(logId)).thenReturn(Optional.of(pendingLog));
        when(repository.save(any(TransferLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransferLog result = transferLogService.markSuccess(
                logId, "target_123", Map.of(), null);

        // Assert
        assertNotNull(result.getMessage());
        assertEquals("Transfer completed successfully", result.getMessage());
    }

    @Test
    void testMarkFailure_ValidLog_UpdatesFailure() {
        // Arrange
        Long logId = 1L;
        String errorMessage = "API connection timeout";
        Map<String, Object> responsePayload = Map.of(
                "error", "timeout",
                "code", 504
        );
        
        TransferLog pendingLog = SellerNexusTestDataBuilder.createTestTransferLog();
        pendingLog.setId(logId);
        pendingLog.setStatus("PENDING");
        pendingLog.setStartedAt(new Timestamp(System.currentTimeMillis() - 10000));
        
        when(repository.findById(logId)).thenReturn(Optional.of(pendingLog));
        when(repository.save(any(TransferLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransferLog result = transferLogService.markFailure(
                logId, errorMessage, responsePayload);

        // Assert
        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
        assertEquals(errorMessage, result.getErrorMessage());
        assertTrue(result.getMessage().contains("Transfer failed"));
        assertTrue(result.getMessage().contains(errorMessage));
        assertNotNull(result.getResponsePayload());
        assertNotNull(result.getFinishedAt());
        assertNotNull(result.getDurationMs());
        assertTrue(result.getDurationMs() > 0);
        
        verify(repository).findById(logId);
        verify(repository).save(any(TransferLog.class));
    }

    @Test
    void testMarkFailure_LogNotFound_ThrowsException() {
        // Arrange
        Long logId = 999L;
        when(repository.findById(logId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transferLogService.markFailure(logId, "Error message", Map.of());
        });
        
        assertTrue(exception.getMessage().contains("TransferLog not found"));
        verify(repository, never()).save(any());
    }

    @Test
    void testMarkFailure_MasksSensitiveData() {
        // Arrange
        Long logId = 1L;
        Map<String, Object> responsePayload = Map.of(
                "password", "secret_password",
                "secret", "secret_key_123",
                "error", "Authentication failed"
        );
        
        TransferLog pendingLog = SellerNexusTestDataBuilder.createTestTransferLog();
        pendingLog.setId(logId);
        pendingLog.setStartedAt(new Timestamp(System.currentTimeMillis()));
        
        when(repository.findById(logId)).thenReturn(Optional.of(pendingLog));
        when(repository.save(any(TransferLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransferLog result = transferLogService.markFailure(
                logId, "Auth error", responsePayload);

        // Assert
        assertNotNull(result.getResponsePayload());
        assertFalse(result.getResponsePayload().contains("secret_password"),
                "Password should be masked");
        assertFalse(result.getResponsePayload().contains("secret_key_123"),
                "Secret should be masked");
        assertTrue(result.getResponsePayload().contains("***MASKED***"),
                "Should contain mask placeholder");
    }

    @Test
    void testGetLogsBySeller_ValidSellerId_ReturnsLogs() {
        // Arrange
        Integer sellerId = 1;
        List<TransferLog> expectedLogs = Arrays.asList(
                SellerNexusTestDataBuilder.createTestTransferLog(),
                SellerNexusTestDataBuilder.createSuccessfulTransferLog()
        );
        
        when(repository.findBySellerIdOrderByStartedAtDesc(sellerId))
                .thenReturn(expectedLogs);

        // Act
        List<TransferLog> result = transferLogService.getLogsBySeller(sellerId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedLogs, result);
        
        verify(repository).findBySellerIdOrderByStartedAtDesc(sellerId);
    }

    @Test
    void testGetLogsBySeller_NoLogs_ReturnsEmptyList() {
        // Arrange
        Integer sellerId = 999;
        when(repository.findBySellerIdOrderByStartedAtDesc(sellerId))
                .thenReturn(Collections.emptyList());

        // Act
        List<TransferLog> result = transferLogService.getLogsBySeller(sellerId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLogsByStatus_ValidStatus_ReturnsFilteredLogs() {
        // Arrange
        String status = "SUCCESS";
        List<TransferLog> successfulLogs = Collections.singletonList(
                SellerNexusTestDataBuilder.createSuccessfulTransferLog()
        );
        
        when(repository.findByStatusOrderByStartedAtDesc(status))
                .thenReturn(successfulLogs);

        // Act
        List<TransferLog> result = transferLogService.getLogsByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SUCCESS", result.get(0).getStatus());
        
        verify(repository).findByStatusOrderByStartedAtDesc(status);
    }

    @Test
    void testGetLogById_ValidId_ReturnsLog() {
        // Arrange
        Long logId = 1L;
        when(repository.findById(logId)).thenReturn(Optional.of(testTransferLog));

        // Act
        TransferLog result = transferLogService.getLogById(logId);

        // Assert
        assertNotNull(result);
        assertEquals(testTransferLog, result);
        assertEquals(logId, result.getId());
        
        verify(repository).findById(logId);
    }

    @Test
    void testGetLogById_InvalidId_ThrowsException() {
        // Arrange
        Long logId = 999L;
        when(repository.findById(logId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transferLogService.getLogById(logId);
        });
        
        assertTrue(exception.getMessage().contains("Transfer log not found"));
        assertTrue(exception.getMessage().contains(logId.toString()));
    }

    @Test
    void testDurationCalculation_ValidTimestamps_CalculatesCorrectly() {
        // Arrange
        Long logId = 1L;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 3500; // 3.5 seconds later
        
        TransferLog pendingLog = SellerNexusTestDataBuilder.createTestTransferLog();
        pendingLog.setId(logId);
        pendingLog.setStartedAt(new Timestamp(startTime));
        
        when(repository.findById(logId)).thenReturn(Optional.of(pendingLog));
        when(repository.save(any(TransferLog.class)))
                .thenAnswer(invocation -> {
                    TransferLog log = invocation.getArgument(0);
                    log.setFinishedAt(new Timestamp(endTime));
                    return log;
                });

        // Act
        TransferLog result = transferLogService.markSuccess(
                logId, "target_123", Map.of(), "Success");

        // Assert
        assertNotNull(result.getDurationMs());
        // Duration should be approximately 3500ms (allowing small margin for test execution)
        assertTrue(result.getDurationMs() >= 3000 && result.getDurationMs() <= 4000,
                "Duration should be around 3500ms, but was: " + result.getDurationMs());
    }
}
