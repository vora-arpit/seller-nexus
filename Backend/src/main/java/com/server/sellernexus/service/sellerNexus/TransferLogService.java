package com.server.sellernexus.service.sellerNexus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.sellernexus.model.sellurNexus.TransferLog;
import com.server.sellernexus.repository.sellerNexus.TransferLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferLogService {

    private final TransferLogRepository repository;
    private final ObjectMapper objectMapper;

    /**
     * Create a new transfer log entry in PENDING state
     */
    public TransferLog createPending(Integer sellerId, Long sourceCredentialId, Long targetCredentialId,
                                      String sourceProductExtId, String platformName, Object requestPayload) {
        TransferLog log = new TransferLog();
        log.setSellerId(sellerId);
        log.setSourceCredentialId(sourceCredentialId);
        log.setTargetCredentialId(targetCredentialId);
        log.setSourceProductExtId(sourceProductExtId);
        log.setPlatformName(platformName);
        log.setStatus("PENDING");
        log.setStartedAt(new Timestamp(System.currentTimeMillis()));
        log.setRequestPayload(toJson(requestPayload));
        return repository.save(log);
    }

    /**
     * Mark transfer as successful
     */
    public TransferLog markSuccess(Long logId, String targetProductExtId, Object responsePayload, String message) {
        TransferLog log = repository.findById(logId).orElseThrow(() -> new RuntimeException("TransferLog not found"));
        log.setTargetProductExtId(targetProductExtId);
        log.setResponsePayload(toJson(responsePayload));
        log.setStatus("SUCCESS");
        log.setMessage(message != null ? message : "Transfer completed successfully");
        log.setFinishedAt(new Timestamp(System.currentTimeMillis()));
        log.setDurationMs(calculateDuration(log.getStartedAt(), log.getFinishedAt()));
        return repository.save(log);
    }

    /**
     * Mark transfer as failed
     */
    public TransferLog markFailure(Long logId, String errorMessage, Object responsePayload) {
        TransferLog log = repository.findById(logId).orElseThrow(() -> new RuntimeException("TransferLog not found"));
        log.setErrorMessage(errorMessage);
        log.setResponsePayload(toJson(responsePayload));
        log.setStatus("FAILED");
        log.setMessage("Transfer failed: " + errorMessage);
        log.setFinishedAt(new Timestamp(System.currentTimeMillis()));
        log.setDurationMs(calculateDuration(log.getStartedAt(), log.getFinishedAt()));
        return repository.save(log);
    }

    /**
     * Get all logs for a seller
     */
    public List<TransferLog> getLogsBySeller(Integer sellerId) {
        return repository.findBySellerIdOrderByStartedAtDesc(sellerId);
    }

    /**
     * Get logs by status
     */
    public List<TransferLog> getLogsByStatus(String status) {
        return repository.findByStatusOrderByStartedAtDesc(status);
    }

    /**
     * Get a single log by ID
     */
    public TransferLog getLogById(Long logId) {
        return repository.findById(logId)
            .orElseThrow(() -> new RuntimeException("Transfer log not found with id: " + logId));
    }

    /**
     * Convert object to JSON string, masking sensitive fields
     */
    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            String json = objectMapper.writeValueAsString(obj);
            // Mask sensitive fields (access tokens, passwords, etc.)
            json = json.replaceAll("\"accessToken\"\\s*:\\s*\"[^\"]+\"", "\"accessToken\":\"***MASKED***\"");
            json = json.replaceAll("\"password\"\\s*:\\s*\"[^\"]+\"", "\"password\":\"***MASKED***\"");
            json = json.replaceAll("\"secret\"\\s*:\\s*\"[^\"]+\"", "\"secret\":\"***MASKED***\"");
            return json;
        } catch (Exception e) {
            return "{\"error\":\"Failed to serialize: " + e.getMessage() + "\"}";
        }
    }

    /**
     * Calculate duration in milliseconds
     */
    private Integer calculateDuration(Timestamp start, Timestamp end) {
        if (start == null || end == null) return null;
        return (int) (end.getTime() - start.getTime());
    }
}
