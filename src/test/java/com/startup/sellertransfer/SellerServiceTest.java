package com.startup.sellertransfer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SellerServiceTest {

    @Autowired
    SellerService sellerService;

    @Test
    void testRegisterSeller_Success() {
        Seller seller = new Seller("John", "john@example.com", "Password@123");
        Seller saved = sellerService.registerSeller(seller);
        assertNotNull(saved.getId());
    }

    @Test
    void testRegisterSeller_InvalidEmail_ThrowsException() {
        Seller seller = new Seller("John", "invalidEmail", "Password@123");
        assertThrows(InvalidEmailException.class, () -> sellerService.registerSeller(seller));
    }

    @Test
    void testLoginSeller_WrongPassword_ThrowsException() {
        assertThrows(AuthenticationFailedException.class,
                () -> sellerService.loginSeller("john@example.com", "wrongPassword"));
    }
}

