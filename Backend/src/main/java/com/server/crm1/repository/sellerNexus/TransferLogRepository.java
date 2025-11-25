package com.server.crm1.repository.sellerNexus;

import com.server.crm1.model.sellurNexus.TransferLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferLogRepository extends JpaRepository<TransferLog, Long> {
    List<TransferLog> findBySellerIdOrderByStartedAtDesc(Integer sellerId);
    List<TransferLog> findByStatusOrderByStartedAtDesc(String status);
}

