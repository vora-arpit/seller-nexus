package com.startup.sellertransfer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Test
void testValidateSourcePlatformAPI_InvalidKey_ThrowsException() {
    assertThrows(InvalidAPIKeyException.class,
            () -> credentialValidator.validateSourcePlatformAPI("invalidKey"));
}

