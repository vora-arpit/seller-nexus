package com.server.crm1.service.sellerNexus;

import com.server.crm1.model.sellurNexus.Seller;
import com.server.crm1.repository.sellerNexus.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    public Seller createSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

    public List<Seller> getAll() {
        return sellerRepository.findAll();
    }
}

