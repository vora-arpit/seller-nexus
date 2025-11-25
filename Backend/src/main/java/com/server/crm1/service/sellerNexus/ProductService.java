package com.server.crm1.service.sellerNexus;

import com.server.crm1.model.sellurNexus.Product;
import com.server.crm1.repository.sellerNexus.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Product saveProduct(Product product) {
        return repository.save(product);
    }

    public List<Product> getSellerProducts(Long sellerId) {
        return repository.findBySellerId(sellerId);
    }
}

