package com.server.sellernexus.controller.sellerNexus;

import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.model.users.User;
import com.server.sellernexus.repository.user.UserRepository;
import com.server.sellernexus.service.sellerNexus.JoomTransferService;
import com.server.sellernexus.service.sellerNexus.PlatformCredentialService;
import com.server.sellernexus.service.sellerNexus.TransferLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller handling JOOM product transfer operations.
 * Responsibilities:
 * - Transfer products between JOOM accounts
 * - View transfer logs and history
 * - Monitor transfer status
 */
@Slf4j
@RestController
@RequestMapping("/api/joom/transfer")
public class JoomTransferController extends BaseJoomController {

    private final PlatformCredentialService credentialService;
    private final JoomTransferService transferService;
    private final TransferLogService transferLogService;

    public JoomTransferController(
            UserRepository userRepository,
            PlatformCredentialService credentialService,
            JoomTransferService transferService,
            TransferLogService transferLogService) {
        super(userRepository);
        this.credentialService = credentialService;
        this.transferService = transferService;
        this.transferLogService = transferLogService;
    }

    /**
     * Transfer a single product from one JOOM account to another.
     * @param body Request body containing sourceCredentialId, targetCredentialId, and sourceProductId
     * @return Transfer result with created product details
     */
    @PostMapping
    public ResponseEntity<?> transferProduct(@RequestBody Map<String, Object> body) {
        try {
            // Parse and validate request parameters
            TransferRequest request = parseTransferRequest(body);
            
            if (!request.isValid()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "sourceCredentialId, targetCredentialId and sourceProductId are required")
                );
            }

            // Authenticate user
            User currentUser = getCurrentUserFromSecurity();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User session not found"));
            }

            // Validate credentials and authorization
            PlatformCredential sourceCred = credentialService.findById(request.sourceId);
            PlatformCredential targetCred = credentialService.findById(request.targetId);
            
            if (!isAuthorizedForCredentials(currentUser, sourceCred, targetCred)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "Not authorized to use one of the credentials")
                );
            }

            // Delegate to transfer service
            Map createResponse = transferService.transferProduct(
                currentUser.getId(),
                sourceCred,
                targetCred,
                request.sourceProductId
            );

            log.info("Successfully transferred product {} from credential {} to {}", 
                request.sourceProductId, request.sourceId, request.targetId);

            return ResponseEntity.ok(createResponse);

        } catch (RuntimeException ex) {
            log.error("Transfer failed: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error during transfer", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Transfer failed: " + ex.getMessage()));
        }
    }

    /**
     * Get transfer logs for the current user.
     * @return List of transfer logs
     */
    @GetMapping("/logs")
    public ResponseEntity<?> getTransferLogs() {
        User currentUser = getCurrentUserFromSecurity();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User session not found"));
        }
        
        return ResponseEntity.ok(transferLogService.getLogsBySeller(currentUser.getId()));
    }

    /**
     * Get a specific transfer log by ID.
     * @param logId Transfer log ID
     * @return Transfer log details
     */
    @GetMapping("/logs/{logId}")
    public ResponseEntity<?> getTransferLog(@PathVariable Long logId) {
        User currentUser = getCurrentUserFromSecurity();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User session not found"));
        }

        try {
            // Could add authorization check here if needed
            return ResponseEntity.ok(transferLogService.getLogById(logId));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Parse transfer request from body.
     */
    private TransferRequest parseTransferRequest(Map<String, Object> body) {
        Long sourceId = body.get("sourceCredentialId") == null ? null : 
            Long.valueOf(String.valueOf(body.get("sourceCredentialId")));
        Long targetId = body.get("targetCredentialId") == null ? null : 
            Long.valueOf(String.valueOf(body.get("targetCredentialId")));
        String sourceProductId = body.get("sourceProductId") == null ? null : 
            String.valueOf(body.get("sourceProductId"));

        return new TransferRequest(sourceId, targetId, sourceProductId);
    }

    /**
     * Check if user is authorized to use both credentials.
     */
    private boolean isAuthorizedForCredentials(User user, PlatformCredential source, PlatformCredential target) {
        if (source.getSeller() == null || !source.getSeller().getId().equals(user.getId())) {
            return false;
        }
        if (target.getSeller() == null || !target.getSeller().getId().equals(user.getId())) {
            return false;
        }
        return true;
    }

    /**
     * Inner class to hold parsed transfer request data.
     */
    private static class TransferRequest {
        Long sourceId;
        Long targetId;
        String sourceProductId;

        TransferRequest(Long sourceId, Long targetId, String sourceProductId) {
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.sourceProductId = sourceProductId;
        }

        boolean isValid() {
            return sourceId != null && targetId != null && 
                   sourceProductId != null && !sourceProductId.isEmpty();
        }
    }
}
