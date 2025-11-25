package com.server.sellernexus.service.sellerNexus;

import com.server.sellernexus.model.sellurNexus.Seller;
import com.server.sellernexus.repository.sellerNexus.SellerRepository;
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

