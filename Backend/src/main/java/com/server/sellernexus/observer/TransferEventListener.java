package com.server.sellernexus.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ============================================
 * OBSERVER DESIGN PATTERN - Listener/Observer
 * ============================================
 * 
 * TransferEventListener - Event Listener (Observer in Observer Pattern)
 * 
 * Purpose: Listens to and reacts to transfer events published by TransferEventPublisher
 * Benefits:
 * - Separation of Concerns: Event handling logic separated from business logic
 * - Extensibility: Can add multiple listeners without modifying publisher
 * - Asynchronous Processing: Can process events independently
 * 
 * Pattern Type: Observer Pattern - Observer/Listener
 * 
 * Responsibilities:
 * - Subscribe to transfer events
 * - React to different event types
 * - Log, notify, or perform actions based on events
 * 
 * Usage:
 * This listener is automatically registered by Spring and will receive
 * all TransferEvent objects published by TransferEventPublisher
 * 
 * Example Use Cases:
 * - Sending real-time updates via WebSocket
 * - Logging transfer progress to database
 * - Sending email/SMS notifications
 * - Updating analytics/metrics
 */
@Component
@RequiredArgsConstructor
public class TransferEventListener {
    
    /**
     * Listen to all transfer events
     * This method is automatically called by Spring when TransferEvent is published
     */
    @EventListener
    public void handleTransferEvent(TransferEvent event) {
        // Log the event (in production, this could be sent to WebSocket, database, etc.)
        System.out.println("[TransferEventListener] Received event: " + event);
        
        // Handle different event types
        switch (event.getStatus()) {
            case STARTED:
                handleTransferStarted(event);
                break;
            case FETCHING_PRODUCTS:
                handleFetchingProducts(event);
                break;
            case PRODUCTS_FETCHED:
                handleProductsFetched(event);
                break;
            case PRODUCT_UPLOADED:
                handleProductUploaded(event);
                break;
            case PRODUCT_FAILED:
                handleProductFailed(event);
                break;
            case COMPLETED:
                handleTransferCompleted(event);
                break;
            case FAILED:
                handleTransferFailed(event);
                break;
            default:
                handleGenericEvent(event);
        }
    }
    
    private void handleTransferStarted(TransferEvent event) {
        System.out.println(String.format(
            "[TransferEventListener] Transfer started for seller %d: %s -> %s (%d products)",
            event.getSellerId(),
            event.getSourcePlatform(),
            event.getDestinationPlatform(),
            event.getTotalProducts()
        ));
        // In production: Send WebSocket notification, update UI, etc.
    }
    
    private void handleFetchingProducts(TransferEvent event) {
        System.out.println(String.format(
            "[TransferEventListener] Fetching products for seller %d from %s",
            event.getSellerId(),
            event.getSourcePlatform()
        ));
    }
    
    private void handleProductsFetched(TransferEvent event) {
        System.out.println(String.format(
            "[TransferEventListener] Products fetched for seller %d",
            event.getSellerId()
        ));
    }
    
    private void handleProductUploaded(TransferEvent event) {
        double progress = event.getProgressPercentage();
        System.out.println(String.format(
            "[TransferEventListener] Product uploaded for seller %d: %s (%.1f%% complete)",
            event.getSellerId(),
            event.getProductId(),
            progress
        ));
        // In production: Update progress bar, send notification, etc.
    }
    
    private void handleProductFailed(TransferEvent event) {
        System.out.println(String.format(
            "[TransferEventListener] Product failed for seller %d: %s - %s",
            event.getSellerId(),
            event.getProductId(),
            event.getMessage()
        ));
        // In production: Log error, send alert, etc.
    }
    
    private void handleTransferCompleted(TransferEvent event) {
        System.out.println(String.format(
            "[TransferEventListener] Transfer completed for seller %d: %s",
            event.getSellerId(),
            event.getMessage()
        ));
        // In production: Send completion notification, update dashboard, etc.
    }
    
    private void handleTransferFailed(TransferEvent event) {
        System.out.println(String.format(
            "[TransferEventListener] Transfer failed for seller %d: %s",
            event.getSellerId(),
            event.getMessage()
        ));
        // In production: Send error notification, log to database, etc.
    }
    
    private void handleGenericEvent(TransferEvent event) {
        System.out.println(String.format(
            "[TransferEventListener] Generic event for seller %d: %s",
            event.getSellerId(),
            event.getMessage()
        ));
    }
}
