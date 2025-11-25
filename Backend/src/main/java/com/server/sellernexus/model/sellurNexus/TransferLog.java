package com.server.sellernexus.model.sellurNexus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "transfer_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "source_credential_id")
    private Long sourceCredentialId;

    @Column(name = "target_credential_id")
    private Long targetCredentialId;

    @Column(name = "platform_name", nullable = false, length = 100)
    private String platformName;

    @Column(name = "source_product_ext_id")
    private String sourceProductExtId;

    @Column(name = "target_product_ext_id")
    private String targetProductExtId;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "PENDING";

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "started_at")
    private Timestamp startedAt;

    @Column(name = "finished_at")
    private Timestamp finishedAt;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "synced_at")
    private Timestamp syncedAt;

    @PrePersist
    protected void onCreate() {
        if (this.syncedAt == null) {
            this.syncedAt = new Timestamp(System.currentTimeMillis());
        }
        if (this.startedAt == null) {
            this.startedAt = new Timestamp(System.currentTimeMillis());
        }
    }
}
