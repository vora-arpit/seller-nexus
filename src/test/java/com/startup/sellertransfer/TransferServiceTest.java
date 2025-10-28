package com.startup.sellertransfer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class TransferServiceTest {

    @Autowired
    TransferService transferService;

    @MockBean
    SourcePlatformAPI sourceAPI;

    @MockBean
    DestinationPlatformAPI destinationAPI;

    @Test
    void testTransferProducts_Success() {
        when(sourceAPI.fetchProducts()).thenReturn(List.of(new Product("Test Product", 10.0)));
        when(destinationAPI.uploadProducts(any())).thenReturn(true);

        boolean result = transferService.transferProducts("sellerId123");
        assertTrue(result);
    }

    @Test
    void testTransferProducts_SourceEmpty_ThrowsException() {
        when(sourceAPI.fetchProducts()).thenReturn(List.of());
        assertThrows(NoProductsFoundException.class, () -> transferService.transferProducts("sellerId123"));
    }
}

