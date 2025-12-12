package com.server.sellernexus.command;

import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.repository.sellerNexus.PlatformCredentialRepository;
import com.server.sellernexus.service.sellerNexus.JoomTransferService;
import java.util.Map;

public class ProductTransferCommand implements TransferCommand {
    private final String productId;
    private final Long sourceCredentialId;
    private final Long targetCredentialId;
    private final Integer sellerId;
    private final JoomTransferService transferService;
    private final PlatformCredentialRepository credentialRepository;

    public ProductTransferCommand(String productId, Long sourceCredentialId,
                                 Long targetCredentialId, Integer sellerId,
                                 JoomTransferService transferService,
                                 PlatformCredentialRepository credentialRepository) {
        this.productId = productId;
        this.sourceCredentialId = sourceCredentialId;
        this.targetCredentialId = targetCredentialId;
        this.sellerId = sellerId;
        this.transferService = transferService;
        this.credentialRepository = credentialRepository;
    }

    @Override
    public TransferResult execute() {
        try {
            PlatformCredential sourceCredential = credentialRepository.findById(sourceCredentialId)
                .orElseThrow(() -> new RuntimeException("Source credential not found"));
            PlatformCredential targetCredential = credentialRepository.findById(targetCredentialId)
                .orElseThrow(() -> new RuntimeException("Target credential not found"));
            
            Map<String, Object> result = transferService.transferProduct(
                sellerId, sourceCredential, targetCredential, productId);
            
            boolean success = result.containsKey("productId");
            String message = success ? "Transfer successful" : "Transfer failed";
            
            return new TransferResult(productId, success, message);
        } catch (Exception e) {
            return new TransferResult(productId, false, e.getMessage());
        }
    }

    @Override
    public void undo() {
        System.out.println("[ProductTransferCommand] Undo not implemented for product: " + productId);
    }
}
