package com.server.crm1.repository.sellerNexus;

import com.server.crm1.model.sellurNexus.PlatformCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PlatformCredentialRepository extends JpaRepository<PlatformCredential, Long> {

    // Allow fetching multiple credentials for same seller + platform (handles duplicates)
    java.util.List<com.server.crm1.model.sellurNexus.PlatformCredential> findAllBySellerIdAndPlatform(Integer sellerId, String platform);

    // Changed to return List because there might be duplicates in the database
    java.util.List<com.server.crm1.model.sellurNexus.PlatformCredential> findAllByExternalMerchantIdAndPlatform(String externalMerchantId, String platform);

}

