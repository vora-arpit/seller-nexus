package com.server.sellernexus.controller.sellerNexus;

import com.server.sellernexus.model.sellurNexus.Product;
import com.server.sellernexus.service.sellerNexus.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    public Product add(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @GetMapping("/seller/{id}")
    public List<Product> getSellerProducts(@PathVariable Long id) {
        return productService.getSellerProducts(id);
    }
}

