package com.server.sellernexus.repository.sellerNexus;

import com.server.sellernexus.model.sellurNexus.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySellerId(Long sellerId);
}

