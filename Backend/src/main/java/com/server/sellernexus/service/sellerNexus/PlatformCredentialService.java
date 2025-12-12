package com.server.sellernexus.service.sellerNexus;

import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.model.users.User;
import com.server.sellernexus.repository.sellerNexus.PlatformCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlatformCredentialService {

    // Service configuration constants
    private static final int FIRST_CREDENTIAL_INDEX = 0;
    private static final String JOOM_PLATFORM = "JOOM";

    private final PlatformCredentialRepository credentialRepo;

    /**
     * Retrieves all platform credentials.
     *
     * @return List of all credentials
     */
    public List<PlatformCredential> findAll() {
        return credentialRepo.findAll();
    }

    /**
     * Persists platform credential.
     *
     * @param credential Credential to save
     * @return Saved credential with generated ID
     */
    public PlatformCredential save(PlatformCredential credential) {
        return credentialRepo.save(credential);
    }

    /**
     * Finds credential by ID.
     *
     * @param id Credential ID
     * @return Found credential
     * @throws RuntimeException if not found
     */
    public PlatformCredential findById(Long id) {
        return credentialRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Credential not found with id: " + id));
    }

    /**
     * Deletes credential by ID.
     *
     * @param id Credential ID to delete
     */
    public void deleteById(Long id) {
        credentialRepo.deleteById(id);
    }

    /**
     * Finds first credential for seller and platform.
     *
     * @param sellerId Seller identifier
     * @param platform Platform name
     * @return First matching credential
     * @throws RuntimeException if no credentials found
     */
    public PlatformCredential findBySellerAndPlatform(Integer sellerId, String platform) {
        List<PlatformCredential> credentials = credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform);
        
        if (credentials.isEmpty()) {
            throw new RuntimeException("User is not connected to " + platform);
        }
        
        return credentials.get(FIRST_CREDENTIAL_INDEX);
    }

    /**
     * Finds all credentials for seller and platform.
     *
     * @param sellerId Seller identifier
     * @param platform Platform name
     * @return List of matching credentials
     */
    public List<PlatformCredential> findAllBySellerAndPlatform(Integer sellerId, String platform) {
        return credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform);
    }

    /**
     * Finds credential by ID, returning Optional.
     *
     * @param id Credential ID
     * @return Optional containing credential if found
     */
    public Optional<PlatformCredential> findByIdOptional(Long id) {
        return credentialRepo.findById(id);
    }

    /**
     * Finds first credential for seller and platform, returning Optional.
     *
     * @param sellerId Seller identifier
     * @param platform Platform name
     * @return Optional containing first credential if found
     */
    public Optional<PlatformCredential> findBySellerAndPlatformOptional(Integer sellerId, String platform) {
        List<PlatformCredential> credentials = credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform);
        return credentials.isEmpty() ? Optional.empty() : Optional.of(credentials.get(FIRST_CREDENTIAL_INDEX));
    }

    /**
     * Finds credentials by external merchant ID and platform.
     *
     * @param externalId External merchant identifier
     * @param platform Platform name
     * @return List of matching credentials
     */
    public List<PlatformCredential> findAllByExternalMerchantIdAndPlatform(String externalId, String platform) {
        return credentialRepo.findAllByExternalMerchantIdAndPlatform(externalId, platform);
    }

    /**
     * Gets credentials summary for current user.
     *
     * @param currentUser Current authenticated user
     * @return List of credential summaries
     */
    public List<Map<String, Object>> getCredentialsSummary(User currentUser) {
        List<PlatformCredential> credentials = credentialRepo.findAllBySellerIdAndPlatform(
            currentUser.getId(), JOOM_PLATFORM
        );

        return credentials.stream()
            .map(this::buildCredentialSummary)
            .collect(Collectors.toList());
    }

    /**
     * Builds summary map for a credential.
     */
    private Map<String, Object> buildCredentialSummary(PlatformCredential credential) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", credential.getId());
        summary.put("label", credential.getLabel());
        summary.put("externalMerchantId", credential.getExternalMerchantId());
        summary.put("createdAt", credential.getCreatedAt());
        summary.put("sellerId", extractSellerId(credential));
        return summary;
    }

    /**
     * Safely extracts seller ID from credential.
     */
    private Integer extractSellerId(PlatformCredential credential) {
        return credential.getSeller() != null ? credential.getSeller().getId() : null;
    }

    /**
     * Deletes credential if user is authorized.
     *
     * @param credentialId Credential ID to delete
     * @param currentUser Current authenticated user
     * @throws RuntimeException if credential not found
     * @throws SecurityException if user not authorized
     */
    public void deleteCredential(Long credentialId, User currentUser) {
        Optional<PlatformCredential> opt = credentialRepo.findById(credentialId);
        
        if (!opt.isPresent()) {
            throw new RuntimeException("Credential not found");
        }

        PlatformCredential cred = opt.get();
        
        if (cred.getSeller() == null || !cred.getSeller().getId().equals(currentUser.getId())) {
            throw new SecurityException("Not authorized to delete this credential");
        }

        credentialRepo.deleteById(credentialId);
    }

    public PlatformCredential mergeOrUpdateCredential(
            PlatformCredential newCredential, 
            String label) {
        
        String externalId = newCredential.getExternalMerchantId();
        Integer sellerId = newCredential.getSeller() != null ? newCredential.getSeller().getId() : null;
        
        System.out.println("=== MERGE DEBUG ===");
        System.out.println("New Credential ID: " + newCredential.getId());
        System.out.println("External Merchant ID: " + externalId);
        System.out.println("Seller ID: " + sellerId);
        System.out.println("Label: " + label);
        
        if (sellerId == null) {
            // No seller, just update label and return
            if (label != null && !label.isEmpty()) {
                newCredential.setLabel(label);
                return credentialRepo.save(newCredential);
            }
            return newCredential;
        }
        
        // Find all credentials with the same external merchant ID AND same seller (user-specific)
        List<PlatformCredential> existingCreds = findAllByExternalMerchantIdAndPlatform(externalId, "JOOM")
            .stream()
            .filter(c -> c.getSeller() != null && c.getSeller().getId().equals(sellerId))
            .collect(Collectors.toList());
        
        System.out.println("Found " + existingCreds.size() + " existing credentials with same externalMerchantId and sellerId");
        for (PlatformCredential ec : existingCreds) {
            System.out.println("  - Credential ID: " + ec.getId() + ", External ID: " + ec.getExternalMerchantId() + ", Label: " + ec.getLabel());
        }

        if (!existingCreds.isEmpty()) {
            // Find if there's one that's NOT the newly created one (same user's duplicate)
            PlatformCredential existingToKeep = existingCreds.stream()
                .filter(c -> !c.getId().equals(newCredential.getId()))
                .findFirst()
                .orElse(null);

            if (existingToKeep != null) {
                // Update the existing credential with new tokens
                existingToKeep.setAccessToken(newCredential.getAccessToken());
                existingToKeep.setRefreshToken(newCredential.getRefreshToken());
                existingToKeep.setExpiresIn(newCredential.getExpiresIn());
                existingToKeep.setExpiryTime(newCredential.getExpiryTime());
                
                if (label != null && !label.isEmpty()) {
                    existingToKeep.setLabel(label);
                }
                
                credentialRepo.save(existingToKeep);
                
                // Delete the newly created duplicate (check if it exists first)
                if (credentialRepo.existsById(newCredential.getId())) {
                    credentialRepo.deleteById(newCredential.getId());
                }
                
                // Delete any other duplicates for THIS USER only
                for (PlatformCredential duplicate : existingCreds) {
                    if (!duplicate.getId().equals(existingToKeep.getId()) && 
                        !duplicate.getId().equals(newCredential.getId()) &&
                        credentialRepo.existsById(duplicate.getId())) {
                        credentialRepo.deleteById(duplicate.getId());
                    }
                }
                
                return existingToKeep;
            } else {
                // Only the new credential exists for this user, just update its label
                if (label != null && !label.isEmpty()) {
                    newCredential.setLabel(label);
                    return credentialRepo.save(newCredential);
                }
                return newCredential;
            }
        } else {
            // No existing credential for this user, just update the label on the new one
            if (label != null && !label.isEmpty()) {
                newCredential.setLabel(label);
                return credentialRepo.save(newCredential);
            }
            return newCredential;
        }
    }
}

