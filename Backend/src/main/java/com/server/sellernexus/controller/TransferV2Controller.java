package com.server.sellernexus.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.sellernexus.circuitbreaker.CircuitBreaker;
import com.server.sellernexus.circuitbreaker.CircuitBreakerRegistry;
import com.server.sellernexus.circuitbreaker.CircuitState;
import com.server.sellernexus.command.BulkTransferExecutor;
import com.server.sellernexus.command.ProductTransferCommand;
import com.server.sellernexus.command.TransferResult;
import com.server.sellernexus.repository.sellerNexus.PlatformCredentialRepository;
import com.server.sellernexus.service.sellerNexus.JoomTransferService;

@RestController
@RequestMapping("/api/v2/transfer")
@CrossOrigin(origins = "*")
public class TransferV2Controller {

    @Autowired
    private JoomTransferService joomTransferService;

    @Autowired
    private BulkTransferExecutor bulkTransferExecutor;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private PlatformCredentialRepository credentialRepository;

    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkTransfer(@RequestBody Map<String, Object> request) {
        try {
            Integer sellerId = Integer.valueOf(request.get("sellerId").toString());
            Long sourceCredentialId = Long.valueOf(request.get("sourceCredentialId").toString());
            Long targetCredentialId = Long.valueOf(request.get("targetCredentialId").toString());
            List<String> productIds = (List<String>) request.get("productIds");

            for (String productId : productIds) {
                ProductTransferCommand command = new ProductTransferCommand(
                    productId, sourceCredentialId, targetCredentialId, sellerId,
                    joomTransferService, credentialRepository);
                bulkTransferExecutor.addCommand(command);
            }

            CompletableFuture<List<TransferResult>> future = bulkTransferExecutor.executeParallel();
            List<TransferResult> results = future.get();

            long successCount = results.stream().filter(TransferResult::isSuccess).count();
            long failureCount = results.size() - successCount;

            Map<String, Object> response = new HashMap<>();
            response.put("totalProducts", productIds.size());
            response.put("successCount", successCount);
            response.put("failureCount", failureCount);
            response.put("results", results);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        Map<String, CircuitBreaker> breakers = circuitBreakerRegistry.getAllBreakers();

        // Check if any circuit breaker is OPEN - if so, system is DOWN
        boolean anyOpen = breakers.values().stream()
            .anyMatch(breaker -> breaker.getState() == CircuitState.OPEN);
        
        health.put("status", anyOpen ? "DOWN" : "UP");
        health.put("circuitBreakers", breakers.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    CircuitBreaker breaker = entry.getValue();
                    Map<String, Object> breakerInfo = new HashMap<>();
                    breakerInfo.put("state", breaker.getState().toString());
                    breakerInfo.put("failureCount", breaker.getFailureCount());
                    breakerInfo.put("lastFailureTime", breaker.getLastFailureTime());
                    return breakerInfo;
                }
            ))
        );

        return ResponseEntity.ok(health);
    }

    @PostMapping("/circuit-breaker/{serviceName}/reset")
    public ResponseEntity<Map<String, Object>> resetCircuitBreaker(@PathVariable String serviceName) {
        try {
            circuitBreakerRegistry.reset(serviceName);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Circuit breaker reset successfully");
            response.put("serviceName", serviceName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
