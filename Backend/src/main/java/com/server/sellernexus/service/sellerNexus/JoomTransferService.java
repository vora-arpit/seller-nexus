package com.server.sellernexus.service.sellerNexus;

import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.model.sellurNexus.TransferLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JoomTransferService {

    private final JoomProductService joomProductService;
    private final TransferLogService transferLogService;

    public Map<String, Object> transferProduct(
            Integer sellerId,
            PlatformCredential sourceCredential,
            PlatformCredential targetCredential,
            String sourceProductId) throws Exception {

        TransferLog transferLog = null;

        try {
            // Create pending transfer log
            transferLog = transferLogService.createPending(
                sellerId,
                sourceCredential.getId(),
                targetCredential.getId(),
                sourceProductId,
                "JOOM",
                Map.of(
                    "sourceProductId", sourceProductId,
                    "sourceCredentialId", sourceCredential.getId(),
                    "targetCredentialId", targetCredential.getId()
                )
            );

            // Fetch source product
            Map sourceProduct = joomProductService.fetchProductById(sourceCredential, sourceProductId);

            if (sourceProduct == null) {
                transferLogService.markFailure(
                    transferLog.getId(),
                    "Unable to resolve product object from source response",
                    Map.of()
                );
                throw new RuntimeException("Unable to resolve product object from source response");
            }

            // Map variants and build create payload
            Map<String, Object> createPayload = buildCreatePayload(sourceProduct);
            
            System.out.println("=== TRANSFER DEBUG ===");
            System.out.println("Source Product ID: " + sourceProductId);
            System.out.println("Create Payload: " + createPayload);

            // Create product on target account
            Map createResponse = joomProductService.createProduct(targetCredential, createPayload);

            // Extract created product ID
            String createdProductId = extractCreatedProductId(createResponse);

            // Mark transfer as successful
            transferLogService.markSuccess(
                transferLog.getId(),
                createdProductId,
                createResponse,
                "Product transferred successfully"
            );

            return createResponse;

        } catch (Exception ex) {
            // Log failure if transfer log was created
            if (transferLog != null) {
                transferLogService.markFailure(
                    transferLog.getId(),
                    "Transfer failed: " + ex.getMessage(),
                    Map.of("exception", ex.getClass().getName(), "message", ex.getMessage())
                );
            }
            throw ex;
        }
    }

    private Map<String, Object> buildCreatePayload(Map sourceProduct) {
        Map<String, Object> createPayload = new HashMap<>();

        // Copy common fields
        String[] copyFields = {"name", "description", "mainImage", "brand", "categoryId", "landingPageUrl", "tags"};
        for (String field : copyFields) {
            if (!sourceProduct.containsKey(field)) continue;
            
            Object value = sourceProduct.get(field);
            
            if ("mainImage".equals(field)) {
                String url = joomProductService.extractImageUrl(value);
                if (url != null) {
                    createPayload.put("mainImage", url);
                }
            } else {
                createPayload.put(field, value);
            }
        }

        // Map variants
        List<Map<String, Object>> mappedVariants = mapVariants(sourceProduct);
        
        if (mappedVariants.isEmpty()) {
            throw new RuntimeException("Source product has no variants and missing sku/price");
        }

        // Ensure product-level SKU exists
        if (sourceProduct.get("sku") != null) {
            createPayload.put("sku", sourceProduct.get("sku"));
        } else if (!mappedVariants.isEmpty()) {
            createPayload.put("sku", mappedVariants.get(0).get("sku"));
        }

        createPayload.put("variants", mappedVariants);

        return createPayload;
    }

    private List<Map<String, Object>> mapVariants(Map sourceProduct) {
        List<Map<String, Object>> mappedVariants = new ArrayList<>();
        
        Object variantsObj = sourceProduct.get("variants");
        
        if (variantsObj instanceof List) {
            List<?> rawVariants = (List<?>) variantsObj;
            int index = 0;
            
            for (Object v : rawVariants) {
                index++;
                
                if (!(v instanceof Map)) continue;
                
                Map variantMap = (Map) v;
                Map<String, Object> newVariant = new HashMap<>();

                // Map SKU
                Object sku = variantMap.get("sku");
                if (sku == null) sku = sourceProduct.get("sku");
                if (sku == null) {
                    sku = (sourceProduct.getOrDefault("id", "p") + "-v" + index).toString();
                }
                newVariant.put("sku", String.valueOf(sku));

                // Map price
                Object price = variantMap.get("price");
                if (price == null) price = sourceProduct.get("price");
                if (price == null) price = sourceProduct.get("msrPrice");
                
                if (price == null) {
                    throw new RuntimeException("Variant price missing. Each variant must have a price.");
                }
                newVariant.put("price", String.valueOf(price));

                // Map currency
                Object currency = variantMap.get("currency");
                if (currency == null) currency = sourceProduct.get("currency");
                if (currency == null) currency = "USD";
                newVariant.put("currency", String.valueOf(currency));

                // Copy optional fields
                if (variantMap.containsKey("inventory")) {
                    newVariant.put("inventory", variantMap.get("inventory"));
                }
                
                if (variantMap.containsKey("mainImage")) {
                    String url = joomProductService.extractImageUrl(variantMap.get("mainImage"));
                    if (url != null) newVariant.put("mainImage", url);
                }
                
                if (variantMap.containsKey("gtin")) {
                    newVariant.put("gtin", variantMap.get("gtin"));
                }

                mappedVariants.add(newVariant);
            }
        }

        // If no variants found, synthesize from top-level fields
        if (mappedVariants.isEmpty()) {
            Object topSku = sourceProduct.get("sku");
            Object topPrice = sourceProduct.get("price") != null ? 
                sourceProduct.get("price") : sourceProduct.get("msrPrice");
            Object topCurrency = sourceProduct.get("currency");

            if (topSku == null || topPrice == null) {
                throw new RuntimeException("Source product has no variants and missing sku/price at product level");
            }

            Map<String, Object> singleVariant = new HashMap<>();
            singleVariant.put("sku", String.valueOf(topSku));
            singleVariant.put("price", String.valueOf(topPrice));
            singleVariant.put("currency", topCurrency == null ? "USD" : String.valueOf(topCurrency));
            
            if (sourceProduct.containsKey("inventory")) {
                singleVariant.put("inventory", sourceProduct.get("inventory"));
            }
            
            if (sourceProduct.containsKey("mainImage")) {
                singleVariant.put("mainImage", sourceProduct.get("mainImage"));
            }
            
            mappedVariants.add(singleVariant);
        }

        return mappedVariants;
    }

    private String extractCreatedProductId(Map createResponse) {
        if (createResponse == null) return null;
        
        Object idObj = createResponse.get("id");
        
        if (idObj == null && createResponse.containsKey("data")) {
            Object data = createResponse.get("data");
            if (data instanceof Map) {
                idObj = ((Map) data).get("id");
            }
        }
        
        return idObj != null ? String.valueOf(idObj) : null;
    }
}
