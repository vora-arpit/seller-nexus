package com.server.sellernexus.observer;

/**
 * ============================================
 * OBSERVER DESIGN PATTERN - Event Status Enum
 * ============================================
 * 
 * TransferStatus - Enumeration of possible transfer states
 * 
 * Purpose: Define all possible states a transfer process can be in
 * Benefits:
 * - Type Safety: Compile-time checking of status values
 * - Clarity: Clear definition of all possible states
 * - Maintainability: Easy to add new states
 * 
 * Pattern Type: Observer Pattern - Event State Definition
 */
public enum TransferStatus {
    /**
     * Transfer process has been initiated
     */
    STARTED,
    
    /**
     * Currently fetching products from source platform
     */
    FETCHING_PRODUCTS,
    
    /**
     * Products have been successfully fetched
     */
    PRODUCTS_FETCHED,
    
    /**
     * A single product has been uploaded successfully
     */
    PRODUCT_UPLOADED,
    
    /**
     * A single product upload has failed
     */
    PRODUCT_FAILED,
    
    /**
     * Transfer process completed successfully
     */
    COMPLETED,
    
    /**
     * Transfer process failed
     */
    FAILED,
    
    /**
     * Transfer is currently in progress
     */
    IN_PROGRESS,
    
    /**
     * Transfer has been paused
     */
    PAUSED,
    
    /**
     * Transfer has been cancelled
     */
    CANCELLED
}
