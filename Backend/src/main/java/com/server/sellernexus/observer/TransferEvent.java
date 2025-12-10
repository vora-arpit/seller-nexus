package com.server.sellernexus.observer;

import org.springframework.context.ApplicationEvent;

/**
 * ============================================
 * OBSERVER DESIGN PATTERN - Event Object
 * ============================================
 * 
 * TransferEvent - Event object in Observer Pattern
 * 
 * Purpose: Carries information about transfer state changes to all listeners
 * Benefits:
 * - Encapsulation: All event data in one object
 * - Type Safety: Strong typing for event handling
 * - Extensibility: Easy to add new event properties
 * 
 * Pattern Type: Observer Pattern - Event/Message Object
 * 
 * This event is published by TransferEventPublisher and consumed by
 * any component that registers as a listener (e.g., WebSocket handlers,
 * logging services, notification services)
 */
public class TransferEvent extends ApplicationEvent {
    
    private final Integer sellerId;
    private final TransferStatus status;
    private final String message;
    
    // Optional fields for detailed event information
    private final String sourcePlatform;
    private final String destinationPlatform;
    private final String transferId;
    private final String productId;
    private final Integer totalProducts;
    private final Integer processedProducts;
    
    /**
     * Full constructor with all fields
     */
    public TransferEvent(Object source, 
                        Integer sellerId, 
                        TransferStatus status, 
                        String message,
                        String sourcePlatform,
                        String destinationPlatform,
                        String transferId,
                        String productId,
                        Integer totalProducts,
                        Integer processedProducts) {
        super(source);
        this.sellerId = sellerId;
        this.status = status;
        this.message = message;
        this.sourcePlatform = sourcePlatform;
        this.destinationPlatform = destinationPlatform;
        this.transferId = transferId;
        this.productId = productId;
        this.totalProducts = totalProducts;
        this.processedProducts = processedProducts;
    }
    
    /**
     * Simplified constructor for basic events
     */
    public TransferEvent(Object source, 
                        Integer sellerId, 
                        TransferStatus status, 
                        String message) {
        this(source, sellerId, status, message, null, null, null, null, null, null);
    }
    
    // Getters
    public Integer getSellerId() {
        return sellerId;
    }
    
    public TransferStatus getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getSourcePlatform() {
        return sourcePlatform;
    }
    
    public String getDestinationPlatform() {
        return destinationPlatform;
    }
    
    public String getTransferId() {
        return transferId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public Integer getTotalProducts() {
        return totalProducts;
    }
    
    public Integer getProcessedProducts() {
        return processedProducts;
    }
    
    /**
     * Calculate progress percentage
     */
    public double getProgressPercentage() {
        if (totalProducts == null || totalProducts == 0) {
            return 0.0;
        }
        return (processedProducts != null ? processedProducts : 0) * 100.0 / totalProducts;
    }
    
    @Override
    public String toString() {
        return String.format("TransferEvent{sellerId=%d, status=%s, message='%s', progress=%d/%d}",
            sellerId, status, message, 
            processedProducts != null ? processedProducts : 0,
            totalProducts != null ? totalProducts : 0);
    }
}
