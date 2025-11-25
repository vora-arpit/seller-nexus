package com.server.sellernexus.controller.sellerNexus;

import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.model.users.User;
import com.server.sellernexus.repository.user.UserRepository;
import com.server.sellernexus.service.sellerNexus.JoomProductService;
import com.server.sellernexus.service.sellerNexus.PlatformCredentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller handling JOOM product operations.
 * Responsibilities:
 * - Fetch products with pagination
 * - Fetch individual product details
 * - Product search and filtering
 */
@Slf4j
@RestController
@RequestMapping("/api/joom/products")
public class JoomProductController extends BaseJoomController {

    private final PlatformCredentialService credentialService;
    private final JoomProductService productService;

    public JoomProductController(
            UserRepository userRepository,
            PlatformCredentialService credentialService,
            JoomProductService productService) {
        super(userRepository);
        this.credentialService = credentialService;
        this.productService = productService;
    }

    /**
     * Get paginated list of products from JOOM.
     * @param page Page number (1-indexed)
     * @param pageSize Number of products per page
     * @param credentialId Optional specific credential ID to use
     * @return Paginated product list
     */
    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(required = false) Long credentialId) {
        try {
            User currentUser = getCurrentUserFromSecurity();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User session not found"));
            }

            log.info("Fetching products for user: {}, credentialId: {}, page: {}, pageSize: {}", 
                currentUser.getId(), credentialId, page, pageSize);

            // Validate page parameters
            if (page < 1) page = 1;
            if (pageSize < 1 || pageSize > 100) pageSize = 50;

            PlatformCredential creds = resolveCredential(currentUser, credentialId);
            
            log.info("Using credential ID: {}, External Merchant ID: {}, Label: {}", 
                creds.getId(), creds.getExternalMerchantId(), creds.getLabel());

            Map productsResponse = productService.fetchProducts(creds, page, pageSize);
            return ResponseEntity.ok(productsResponse);

        } catch (RuntimeException ex) {
            log.error("Failed to fetch products", ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error fetching products", ex);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Failed to fetch products from JOOM"));
        }
    }

    /**
     * Get a specific product by ID.
     * @param productId JOOM product ID
     * @param credentialId Optional specific credential ID to use
     * @return Product details
     */
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(
            @PathVariable String productId,
            @RequestParam(required = false) Long credentialId) {
        try {
            User currentUser = getCurrentUserFromSecurity();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User session not found"));
            }

            PlatformCredential creds = resolveCredential(currentUser, credentialId);

            Map product = productService.fetchProductById(creds, productId);
            
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(product);

        } catch (RuntimeException ex) {
            log.error("Failed to fetch product: {}", productId, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Unexpected error fetching product: {}", productId, ex);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Failed to fetch product from JOOM"));
        }
    }

    /**
     * Helper method to resolve the credential to use for the request.
     * Either uses the specified credentialId or the user's default JOOM credential.
     */
    private PlatformCredential resolveCredential(User currentUser, Long credentialId) {
        PlatformCredential creds;

        if (credentialId != null) {
            creds = credentialService.findById(credentialId);
            
            // Authorization check
            if (creds.getSeller() == null || !creds.getSeller().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Not authorized to use this credential");
            }
        } else {
            creds = credentialService.findBySellerAndPlatform(currentUser.getId(), "JOOM");
        }

        return creds;
    }
}
