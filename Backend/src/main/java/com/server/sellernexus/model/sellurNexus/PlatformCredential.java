package com.server.sellernexus.model.sellurNexus;

import com.server.sellernexus.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_credential")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // JOOM, FLIPKART, etc.
    @Column(name = "platform", nullable = false, length = 100)
    private String platform;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    private User seller;


    @Column(name = "access_token", length = 500)
    private String accessToken;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "expires_in")
    private Integer expiresIn;

    @Column(name = "expiry_time")
    private Long expiryTime;

    @Column(name = "external_merchant_id", length = 255)
    private String externalMerchantId;

    @Column(name = "label", length = 255)
    private String label;

    @Column(name = "api_key", length = 255)
    private String apiKey;

    @Column(name = "api_secret", length = 255)
    private String apiSecret;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ============================================
    // BUILDER DESIGN PATTERN IMPLEMENTATION
    // ============================================
    
    /**
     * Builder Pattern for creating PlatformCredential objects
     * Provides a fluent interface for object construction
     * Useful when dealing with objects that have many fields
     */
    public static class Builder {
        private Long id;
        private String platform;
        private User seller;
        private String accessToken;
        private String refreshToken;
        private Integer expiresIn;
        private Long expiryTime;
        private String externalMerchantId;
        private String label;
        private String apiKey;
        private String apiSecret;
        private LocalDateTime createdAt;

        public Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder platform(String platform) {
            this.platform = platform;
            return this;
        }

        public Builder seller(User seller) {
            this.seller = seller;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder expiresIn(Integer expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder expiryTime(Long expiryTime) {
            this.expiryTime = expiryTime;
            return this;
        }

        public Builder externalMerchantId(String externalMerchantId) {
            this.externalMerchantId = externalMerchantId;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PlatformCredential build() {
            PlatformCredential credential = new PlatformCredential();
            credential.id = this.id;
            credential.platform = this.platform;
            credential.seller = this.seller;
            credential.accessToken = this.accessToken;
            credential.refreshToken = this.refreshToken;
            credential.expiresIn = this.expiresIn;
            credential.expiryTime = this.expiryTime;
            credential.externalMerchantId = this.externalMerchantId;
            credential.label = this.label;
            credential.apiKey = this.apiKey;
            credential.apiSecret = this.apiSecret;
            credential.createdAt = this.createdAt;
            return credential;
        }
    }

    /**
     * Static factory method to create a new Builder instance
     * Usage: PlatformCredential.builder().platform("JOOM").seller(user).build()
     */
    public static Builder builder() {
        return new Builder();
    }
}
