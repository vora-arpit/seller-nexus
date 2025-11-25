package com.server.sellernexus.controller.sellerNexus;

import com.server.sellernexus.model.sellurNexus.TransferLog;
import com.server.sellernexus.service.sellerNexus.TransferService;
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
