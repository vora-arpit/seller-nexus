package com.server.sellernexus.service.sellerNexus;

import com.server.sellernexus.model.sellurNexus.Product;
import com.server.sellernexus.model.sellurNexus.Seller;
import com.server.sellernexus.model.sellurNexus.TransferLog;
import com.server.sellernexus.repository.sellerNexus.ProductRepository;
import com.server.sellernexus.repository.sellerNexus.SellerRepository;
import com.server.sellernexus.repository.sellerNexus.TransferLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferLogRepository logRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    public TransferLog transferProducts(Long sellerId, Long productId, String platformName) {
        // Validate seller exists
        if (!sellerRepository.existsById(sellerId)) {
            throw new RuntimeException("Seller not found: " + sellerId);
        }

        // Validate product exists if provided
        if (productId != null && !productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found: " + productId);
        }

        TransferLog log = new TransferLog();
        log.setSellerId(sellerId.intValue());
        log.setProductId(productId != null ? productId.intValue() : null);
        log.setPlatformName(platformName);
        log.setStatus("TRANSFER_STARTED");
        log.setMessage("Product sync initiated");

        return logRepository.save(log);
    }
}
