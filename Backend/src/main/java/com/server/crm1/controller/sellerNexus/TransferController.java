package com.server.crm1.controller.sellerNexus;

import com.server.crm1.model.sellurNexus.TransferLog;
import com.server.crm1.service.sellerNexus.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/start")
    public TransferLog startTransfer(
            @RequestParam Long sellerId,
            @RequestParam(required = false) Long productId,
            @RequestParam String platformName) {

        return transferService.transferProducts(sellerId, productId, platformName);
    }
}
