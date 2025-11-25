package com.server.crm1.controller.sellerNexus;

import com.server.crm1.model.sellurNexus.Seller;
import com.server.crm1.service.sellerNexus.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/create")
    public Seller create(@RequestBody Seller seller) {
        return sellerService.createSeller(seller);
    }

    @GetMapping("/all")
    public List<Seller> all() {
        return sellerService.getAll();
    }
}

