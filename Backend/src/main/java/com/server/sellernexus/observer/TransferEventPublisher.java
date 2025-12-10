package com.server.sellernexus.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * ============================================
 * OBSERVER DESIGN PATTERN - Publisher/Subject
 * ============================================
 * 
 * TransferEventPublisher - Event Publisher (Subject in Observer Pattern)
 * 
 * Purpose: Publishes transfer events to all registered observers/listeners
 * Benefits:
 * - Decoupling: Publisher doesn't know about specific listeners
 * - Scalability: Multiple listeners can subscribe without modifying publisher
 * - Real-time Communication: Immediate notification to all interested parties
 * - Spring Integration: Uses Spring's ApplicationEventPublisher
 * 
 * Pattern Type: Observer Pattern - Subject/Publisher
 * 
 * Responsibilities:
 * - Publish events when transfer state changes
 * - Provide convenience methods for different event types
 * - Maintain no knowledge of who receives the events
 * 
 * Usage Example:
 * transferEventPublisher.publishTransferStarted(sellerId, "JOOM", "AMAZON", 10);
 * transferEventPublisher.publishProductUploaded(sellerId, "PRD123", 5, 10);
 * transferEventPublisher.publishTransferCompleted(sellerId, 10, 9, 1);
 */
@Component
@RequiredArgsConstructor
public class TransferEventPublisher {
    
    // Spring's event publisher - handles actual event distribution
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Publish event when transfer starts
     */
    public void publishTransferStarted(Integer sellerId, 
                                      String sourcePlatform, 
                                      String destPlatform, 
                                      int totalProducts) {
        String message = String.format("Starting transfer of %d products from %s to %s",
            totalProducts, sourcePlatform, destPlatform);
        
        TransferEvent event = new TransferEvent(
            this,
            sellerId,
            TransferStatus.STARTED,
            message,
            sourcePlatform,
            destPlatform,
            null,
            null,
            totalProducts,
            0
        );
        
        System.out.println("[TransferEventPublisher] Publishing: " + event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publish event when products are being fetched
     */
    public void publishFetchingProducts(Integer sellerId, String sourcePlatform) {
        String message = "Fetching products from " + sourcePlatform;
        
        TransferEvent event = new TransferEvent(
            this,
            sellerId,
            TransferStatus.FETCHING_PRODUCTS,
            message
        );
        
        System.out.println("[TransferEventPublisher] Publishing: " + event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publish event when products are fetched successfully
     */
    public void publishProductsFetched(Integer sellerId, String sourcePlatform, int productCount) {
        String message = String.format("Successfully fetched %d products from %s",
            productCount, sourcePlatform);
        
        TransferEvent event = new TransferEvent(
            this,
            sellerId,
            TransferStatus.PRODUCTS_FETCHED,
            message
        );
        
        System.out.println("[TransferEventPublisher] Publishing: " + event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publish event when a product is successfully uploaded
     */
    public void publishProductUploaded(Integer sellerId, 
                                      String productId, 
                                      int processedProducts, 
                                      int totalProducts) {
        String message = String.format("Product %s uploaded successfully (%d/%d)",
            productId, processedProducts, totalProducts);
        
        TransferEvent event = new TransferEvent(
            this,
            sellerId,
            TransferStatus.PRODUCT_UPLOADED,
            message,
            null,
            null,
            null,
            productId,
            totalProducts,
            processedProducts
        );
        
        System.out.println("[TransferEventPublisher] Publishing: " + event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publish event when a product upload fails
     */
    public void publishProductFailed(Integer sellerId, 
                                    String productId, 
                                    String errorMessage,
                                    int processedProducts, 
                                    int totalProducts) {
        String message = String.format("Product %s upload failed: %s (%d/%d)",
            productId, errorMessage, processedProducts, totalProducts);
        
        TransferEvent event = new TransferEvent(
            this,
            sellerId,
            TransferStatus.PRODUCT_FAILED,
            message,
            null,
            null,
            null,
            productId,
            totalProducts,
            processedProducts
        );
        
        System.out.println("[TransferEventPublisher] Publishing: " + event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publish event when transfer completes successfully
     */
    public void publishTransferCompleted(Integer sellerId, 
                                        int totalProducts, 
                                        int successCount, 
                                        int failCount) {
        String message = String.format("Transfer completed: %d successful, %d failed out of %d total",
            successCount, failCount, totalProducts);
        
        TransferEvent event = new TransferEvent(
            this,
            sellerId,
            TransferStatus.COMPLETED,
            message,
            null,
            null,
            null,
            null,
            totalProducts,
            totalProducts
        );
        
        System.out.println("[TransferEventPublisher] Publishing: " + event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publish event when transfer fails completely
     */
    public void publishTransferFailed(Integer sellerId, String errorMessage) {
        String message = "Transfer failed: " + errorMessage;
        
        TransferEvent event = new TransferEvent(
            this,
            sellerId,
            TransferStatus.FAILED,
            message
        );
        
        System.out.println("[TransferEventPublisher] Publishing: " + event);
        eventPublisher.publishEvent(event);
    }
    
    /**
     * Publish custom transfer event
     */
    public void publishEvent(TransferEvent event) {
        System.out.println("[TransferEventPublisher] Publishing custom event: " + event);
        eventPublisher.publishEvent(event);
    }
}
