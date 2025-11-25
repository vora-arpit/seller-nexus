package com.server.crm1.controller.sellerNexus;

import com.server.crm1.model.sellurNexus.PlatformCredential;
import com.server.crm1.model.users.User;
import com.server.crm1.repository.user.UserRepository;
import com.server.crm1.service.sellerNexus.JoomAuthService;
import com.server.crm1.service.sellerNexus.PlatformCredentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * Controller handling JOOM authentication and credential management.
 * Responsibilities:
 * - OAuth authorization flow (authorize, callback)
 * - Credential CRUD operations
 * - Token management (test, refresh)
 */
@Slf4j
@RestController
@RequestMapping("/api/joom/auth")
public class JoomAuthController extends BaseJoomController {

    private final JoomAuthService authService;
    private final PlatformCredentialService credentialService;

    public JoomAuthController(
            UserRepository userRepository,
            JoomAuthService authService,
            PlatformCredentialService credentialService) {
        super(userRepository);
        this.authService = authService;
        this.credentialService = credentialService;
    }

    /**
     * Generate JOOM OAuth authorization URL.
     * @param clientId JOOM client ID
     * @param userId User ID to associate with the credential
     * @param label Optional label for the credential
     * @param clientSecret Optional client secret for non-default clients
     * @return Authorization URL to redirect user to JOOM
     */
    @GetMapping("/authorize")
    public ResponseEntity<String> authorize(
            @RequestParam String clientId,
            @RequestParam Integer userId,
            @RequestParam(required = false) String label,
            @RequestParam(required = false) String clientSecret) {
        
        String url = authService.getAuthorizationUrl(clientId, userId, label);
        
        // Store client secret transiently if provided
        if (clientSecret != null && !clientSecret.isEmpty()) {
            try {
                java.net.URI u = new java.net.URI(url);
                String query = u.getQuery();
                for (String part : query.split("&")) {
                    if (part.startsWith("state=")) {
                        String state = java.net.URLDecoder.decode(
                            part.substring(6), 
                            java.nio.charset.StandardCharsets.UTF_8.toString()
                        );
                        authService.storeSecretForState(state, clientSecret);
                        break;
                    }
                }
            } catch (Exception ex) {
                log.warn("Failed to store client secret for state", ex);
            }
        }
        
        return ResponseEntity.ok(url);
    }

    /**
     * OAuth callback endpoint - exchanges authorization code for access token.
     * @param code Authorization code from JOOM
     * @param state State parameter containing user ID and label
     * @return Redirect to frontend with status
     */
    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam String code,
            @RequestParam(required = false) String state) {

        User user = null;
        String label = null;

        // Parse state to get user and label
        if (state != null && !state.isEmpty()) {
            try {
                Map<String, Object> payload = authService.parseState(state);
                Integer userId = ((Number) payload.get("uid")).intValue();
                label = payload.get("label") != null ? payload.get("label").toString() : null;
                user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found from state"));
            } catch (Exception ex) {
                log.warn("Failed to parse state, falling back to session user", ex);
                user = getCurrentUserFromSecurity();
            }
        } else {
            user = getCurrentUserFromSecurity();
        }

        if (user == null) {
            return redirectToFrontend("error", "missing_state_or_session", null);
        }

        // Determine client credentials
        String usedClientId = null;
        String usedClientSecret = null;
        
        try {
            if (state != null) {
                Map<String, Object> payload = authService.parseState(state);
                usedClientId = payload.get("cid") != null ? payload.get("cid").toString() : null;
            }
        } catch (Exception ex) {
            log.debug("Failed to extract client ID from state", ex);
        }

        if (state != null) {
            usedClientSecret = authService.retrieveAndRemoveSecretForState(state);
        }

        // Fallback to default credentials
        if (usedClientId == null) usedClientId = "31594d8557863747";
        if (usedClientSecret == null && "31594d8557863747".equals(usedClientId)) {
            usedClientSecret = "a3c764ecd75d2bd5e5675a0e2ef4a217";
        }

        if (usedClientSecret == null) {
            return redirectToFrontend("error", "missing_client_secret", null);
        }

        // Exchange code for token
        PlatformCredential saved = authService.exchangeCodeForToken(user, usedClientId, usedClientSecret, code);

        // Merge or update credential to avoid duplicates
        credentialService.mergeOrUpdateCredential(saved, label);

        return redirectToFrontend("success", null, label);
    }

    /**
     * Test if the current user's access token is valid.
     * @return Success message if token is valid
     */
    @GetMapping("/test-token")
    public ResponseEntity<?> testToken() {
        User currentUser = getCurrentUserFromSecurity();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User session not found"));
        }

        PlatformCredential creds = credentialService.findBySellerAndPlatform(currentUser.getId(), "JOOM");
        authService.testAccessToken(creds.getAccessToken());

        return ResponseEntity.ok(Map.of("status", "valid", "message", "Token is valid"));
    }

    /**
     * Refresh the access token for the current user.
     * @return Updated platform credential
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<PlatformCredential> refreshToken() {
        User currentUser = getCurrentUserFromSecurity();
        if (currentUser == null) {
            throw new RuntimeException("User session not found");
        }

        PlatformCredential creds = credentialService.findBySellerAndPlatform(currentUser.getId(), "JOOM");
        return ResponseEntity.ok(authService.refreshAccessToken(creds));
    }

    /**
     * Get all JOOM credentials for the current user.
     * @return List of credential summaries
     */
    @GetMapping("/credentials")
    public ResponseEntity<?> getCredentials() {
        User currentUser = getCurrentUserFromSecurity();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User session not found"));
        }

        return ResponseEntity.ok(credentialService.getCredentialsSummary(currentUser));
    }

    /**
     * Delete a specific credential by ID.
     * @param id Credential ID to delete
     * @return Success or error response
     */
    @DeleteMapping("/credentials/{id}")
    public ResponseEntity<?> deleteCredential(@PathVariable Long id) {
        User currentUser = getCurrentUserFromSecurity();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User session not found"));
        }

        try {
            credentialService.deleteCredential(id, currentUser);
            return ResponseEntity.ok(Map.of("status", "deleted", "id", id));
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            if (ex.getMessage().contains("Not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", ex.getMessage()));
            }
            throw ex;
        }
    }

    /**
     * Helper method to create redirect response to frontend.
     */
    private ResponseEntity<Void> redirectToFrontend(String status, String reason, String label) {
        HttpHeaders headers = new HttpHeaders();
        try {
            StringBuilder redirect = new StringBuilder("http://localhost:4200/selernexus");
            
            if ("error".equals(status)) {
                redirect.append("/joom-login");
            }
            
            redirect.append("?status=").append(status);
            
            if (reason != null) {
                redirect.append("&reason=").append(reason);
            }
            
            if (label != null && !label.isEmpty()) {
                redirect.append("&label=").append(
                    java.net.URLEncoder.encode(label, java.nio.charset.StandardCharsets.UTF_8.toString())
                );
            }
            
            headers.setLocation(URI.create(redirect.toString()));
        } catch (Exception ex) {
            log.error("Failed to build redirect URL", ex);
            headers.setLocation(URI.create("http://localhost:4200/selernexus/joom-login?status=" + status));
        }
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
