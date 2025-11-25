package com.server.crm1.service.sellerNexus;

import com.server.crm1.model.sellurNexus.PlatformCredential;
import com.server.crm1.model.users.User;
import com.server.crm1.repository.sellerNexus.PlatformCredentialRepository;
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

    private final PlatformCredentialRepository credentialRepo;

    public PlatformCredential save(PlatformCredential credential) {
        return credentialRepo.save(credential);
    }

    public PlatformCredential findById(Long id) {
        return credentialRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Credential not found with id: " + id));
    }

    public PlatformCredential findBySellerAndPlatform(Integer sellerId, String platform) {
        List<PlatformCredential> credentials = credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform);
        
        if (credentials.isEmpty()) {
            throw new RuntimeException("User is not connected to " + platform);
        }
        
        // Return the first one (or you could add logic to choose the most recent)
        return credentials.get(0);
    }

    public List<PlatformCredential> findAllBySellerAndPlatform(Integer sellerId, String platform) {
        return credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform);
    }

    public Optional<PlatformCredential> findByIdOptional(Long id) {
        return credentialRepo.findById(id);
    }

    public Optional<PlatformCredential> findBySellerAndPlatformOptional(Integer sellerId, String platform) {
        List<PlatformCredential> credentials = credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform);
        return credentials.isEmpty() ? Optional.empty() : Optional.of(credentials.get(0));
    }

    public List<PlatformCredential> findAllByExternalMerchantIdAndPlatform(String externalId, String platform) {
        return credentialRepo.findAllByExternalMerchantIdAndPlatform(externalId, platform);
    }

    public List<Map<String, Object>> getCredentialsSummary(User currentUser) {
        List<PlatformCredential> creds = credentialRepo.findAllBySellerIdAndPlatform(
            currentUser.getId(), "JOOM"
        );

        List<Map<String, Object>> result = new ArrayList<>();
        for (PlatformCredential c : creds) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("id", c.getId());
            summary.put("label", c.getLabel());
            summary.put("externalMerchantId", c.getExternalMerchantId());
            summary.put("createdAt", c.getCreatedAt());
            result.add(summary);
        }

        return result;
    }

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

