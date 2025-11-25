package com.server.sellernexus.service.sellerNexus;

import com.server.sellernexus.exception.UnauthorizedAccessException;
import com.server.sellernexus.model.sellurNexus.PlatformCredential;
import com.server.sellernexus.model.users.User;
import com.server.sellernexus.repository.sellerNexus.PlatformCredentialRepository;
import com.server.sellernexus.util.SellerNexusTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformCredentialServiceTest {

    @Mock
    private PlatformCredentialRepository credentialRepo;

    @InjectMocks
    private PlatformCredentialService credentialService;

    private User testUser;
    private PlatformCredential testCredential;

    @BeforeEach
    void setUp() {
        testUser = SellerNexusTestDataBuilder.createTestUser();
        testUser.setId(1);
        
        testCredential = SellerNexusTestDataBuilder.createTestCredential();
        testCredential.setId(1L);
        testCredential.setSeller(testUser);
    }

    @Test
    void testSave_ValidCredential_SavesSuccessfully() {
        // Arrange
        when(credentialRepo.save(testCredential)).thenReturn(testCredential);

        // Act
        PlatformCredential result = credentialService.save(testCredential);

        // Assert
        assertNotNull(result);
        assertEquals(testCredential, result);
        verify(credentialRepo, times(1)).save(testCredential);
    }

    @Test
    void testFindById_ValidId_ReturnsCredential() {
        // Arrange
        Long credentialId = 1L;
        when(credentialRepo.findById(credentialId)).thenReturn(Optional.of(testCredential));

        // Act
        PlatformCredential result = credentialService.findById(credentialId);

        // Assert
        assertNotNull(result);
        assertEquals(testCredential, result);
        assertEquals(credentialId, result.getId());
        verify(credentialRepo).findById(credentialId);
    }

    @Test
    void testFindById_InvalidId_ThrowsException() {
        // Arrange
        Long credentialId = 999L;
        when(credentialRepo.findById(credentialId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            credentialService.findById(credentialId);
        });
        
        assertTrue(exception.getMessage().contains("Credential not found"));
        assertTrue(exception.getMessage().contains(credentialId.toString()));
    }

    @Test
    void testFindBySellerAndPlatform_CredentialExists_ReturnsFirst() {
        // Arrange
        Integer sellerId = 1;
        String platform = "JOOM";
        List<PlatformCredential> credentials = Collections.singletonList(testCredential);
        
        when(credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform))
                .thenReturn(credentials);

        // Act
        PlatformCredential result = credentialService.findBySellerAndPlatform(sellerId, platform);

        // Assert
        assertNotNull(result);
        assertEquals(testCredential, result);
        verify(credentialRepo).findAllBySellerIdAndPlatform(sellerId, platform);
    }

    @Test
    void testFindBySellerAndPlatform_MultipleCredentials_ReturnsFirst() {
        // Arrange
        Integer sellerId = 1;
        String platform = "JOOM";
        
        PlatformCredential credential1 = SellerNexusTestDataBuilder.createTestCredential();
        credential1.setId(1L);
        PlatformCredential credential2 = SellerNexusTestDataBuilder.createTestCredential();
        credential2.setId(2L);
        
        List<PlatformCredential> credentials = Arrays.asList(credential1, credential2);
        
        when(credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform))
                .thenReturn(credentials);

        // Act
        PlatformCredential result = credentialService.findBySellerAndPlatform(sellerId, platform);

        // Assert
        assertNotNull(result);
        assertEquals(credential1.getId(), result.getId());
    }

    @Test
    void testFindBySellerAndPlatform_NoCredentials_ThrowsException() {
        // Arrange
        Integer sellerId = 999;
        String platform = "JOOM";
        
        when(credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            credentialService.findBySellerAndPlatform(sellerId, platform);
        });
        
        assertTrue(exception.getMessage().contains("User is not connected to"));
        assertTrue(exception.getMessage().contains(platform));
    }

    @Test
    void testFindAllBySellerAndPlatform_ReturnsAllCredentials() {
        // Arrange
        Integer sellerId = 1;
        String platform = "JOOM";
        List<PlatformCredential> credentials = Arrays.asList(testCredential);
        
        when(credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform))
                .thenReturn(credentials);

        // Act
        List<PlatformCredential> result = credentialService.findAllBySellerAndPlatform(sellerId, platform);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCredential, result.get(0));
    }

    @Test
    void testFindByIdOptional_ValidId_ReturnsOptionalWithValue() {
        // Arrange
        Long credentialId = 1L;
        when(credentialRepo.findById(credentialId)).thenReturn(Optional.of(testCredential));

        // Act
        Optional<PlatformCredential> result = credentialService.findByIdOptional(credentialId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCredential, result.get());
    }

    @Test
    void testFindByIdOptional_InvalidId_ReturnsEmptyOptional() {
        // Arrange
        Long credentialId = 999L;
        when(credentialRepo.findById(credentialId)).thenReturn(Optional.empty());

        // Act
        Optional<PlatformCredential> result = credentialService.findByIdOptional(credentialId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindBySellerAndPlatformOptional_CredentialExists_ReturnsOptionalWithValue() {
        // Arrange
        Integer sellerId = 1;
        String platform = "JOOM";
        List<PlatformCredential> credentials = Collections.singletonList(testCredential);
        
        when(credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform))
                .thenReturn(credentials);

        // Act
        Optional<PlatformCredential> result = credentialService.findBySellerAndPlatformOptional(sellerId, platform);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCredential, result.get());
    }

    @Test
    void testFindBySellerAndPlatformOptional_NoCredentials_ReturnsEmptyOptional() {
        // Arrange
        Integer sellerId = 999;
        String platform = "JOOM";
        
        when(credentialRepo.findAllBySellerIdAndPlatform(sellerId, platform))
                .thenReturn(Collections.emptyList());

        // Act
        Optional<PlatformCredential> result = credentialService.findBySellerAndPlatformOptional(sellerId, platform);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCredentialsSummary_ReturnsFormattedSummary() {
        // Arrange
        List<PlatformCredential> credentials = Arrays.asList(testCredential);
        
        when(credentialRepo.findAllBySellerIdAndPlatform(testUser.getId(), "JOOM"))
                .thenReturn(credentials);

        // Act
        List<Map<String, Object>> result = credentialService.getCredentialsSummary(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        Map<String, Object> summary = result.get(0);
        assertEquals(testCredential.getId(), summary.get("id"));
        assertEquals(testCredential.getLabel(), summary.get("label"));
        assertEquals(testCredential.getExternalMerchantId(), summary.get("externalMerchantId"));
        assertNotNull(summary.get("createdAt"));
    }

    @Test
    void testDeleteCredential_ValidOwner_DeletesSuccessfully() {
        // Arrange
        Long credentialId = 1L;
        
        when(credentialRepo.findById(credentialId)).thenReturn(Optional.of(testCredential));

        // Act
        credentialService.deleteCredential(credentialId, testUser);

        // Assert
        verify(credentialRepo).findById(credentialId);
        verify(credentialRepo).deleteById(credentialId);
    }

    @Test
    void testDeleteCredential_CredentialNotFound_ThrowsException() {
        // Arrange
        Long credentialId = 999L;
        
        when(credentialRepo.findById(credentialId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            credentialService.deleteCredential(credentialId, testUser);
        });
        
        assertTrue(exception.getMessage().contains("Credential not found"));
        verify(credentialRepo, never()).deleteById(any());
    }

    @Test
    void testDeleteCredential_UnauthorizedUser_ThrowsSecurityException() {
        // Arrange
        Long credentialId = 1L;
        User anotherUser = SellerNexusTestDataBuilder.createTestUser();
        anotherUser.setId(999);
        
        when(credentialRepo.findById(credentialId)).thenReturn(Optional.of(testCredential));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            credentialService.deleteCredential(credentialId, anotherUser);
        });
        
        assertTrue(exception.getMessage().contains("Not authorized"));
        verify(credentialRepo, never()).deleteById(any());
    }

    @Test
    void testMergeOrUpdateCredential_NoDuplicates_UpdatesLabel() {
        // Arrange
        String label = "My Joom Account";
        PlatformCredential newCred = SellerNexusTestDataBuilder.createTestCredential();
        newCred.setId(5L);
        newCred.setSeller(testUser);
        
        when(credentialRepo.findAllByExternalMerchantIdAndPlatform(
                newCred.getExternalMerchantId(), "JOOM"))
                .thenReturn(Collections.emptyList());
        
        when(credentialRepo.save(any(PlatformCredential.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PlatformCredential result = credentialService.mergeOrUpdateCredential(newCred, label);

        // Assert
        assertNotNull(result);
        assertEquals(label, result.getLabel());
        verify(credentialRepo).save(any(PlatformCredential.class));
    }

    @Test
    void testMergeOrUpdateCredential_DuplicateExists_MergesAndDeletesNew() {
        // Arrange
        String label = "Updated Label";
        
        // Existing credential
        PlatformCredential existingCred = SellerNexusTestDataBuilder.createTestCredential();
        existingCred.setId(1L);
        existingCred.setSeller(testUser);
        existingCred.setAccessToken("old_access_token");
        
        // New duplicate credential
        PlatformCredential newCred = SellerNexusTestDataBuilder.createTestCredential();
        newCred.setId(2L);
        newCred.setSeller(testUser);
        newCred.setAccessToken("new_access_token");
        newCred.setRefreshToken("new_refresh_token");
        
        when(credentialRepo.findAllByExternalMerchantIdAndPlatform(
                newCred.getExternalMerchantId(), "JOOM"))
                .thenReturn(Arrays.asList(existingCred, newCred));
        
        when(credentialRepo.save(any(PlatformCredential.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        when(credentialRepo.existsById(newCred.getId())).thenReturn(true);

        // Act
        PlatformCredential result = credentialService.mergeOrUpdateCredential(newCred, label);

        // Assert
        assertNotNull(result);
        assertEquals(existingCred.getId(), result.getId());
        assertEquals("new_access_token", result.getAccessToken());
        assertEquals("new_refresh_token", result.getRefreshToken());
        assertEquals(label, result.getLabel());
        
        verify(credentialRepo).save(existingCred);
        verify(credentialRepo).deleteById(newCred.getId());
    }

    @Test
    void testMergeOrUpdateCredential_MultipleDuplicates_DeletesAll() {
        // Arrange
        String label = "Merged Account";
        
        PlatformCredential existingCred1 = SellerNexusTestDataBuilder.createTestCredential();
        existingCred1.setId(1L);
        existingCred1.setSeller(testUser);
        
        PlatformCredential existingCred2 = SellerNexusTestDataBuilder.createTestCredential();
        existingCred2.setId(2L);
        existingCred2.setSeller(testUser);
        
        PlatformCredential newCred = SellerNexusTestDataBuilder.createTestCredential();
        newCred.setId(3L);
        newCred.setSeller(testUser);
        newCred.setAccessToken("newest_token");
        
        when(credentialRepo.findAllByExternalMerchantIdAndPlatform(
                newCred.getExternalMerchantId(), "JOOM"))
                .thenReturn(Arrays.asList(existingCred1, existingCred2, newCred));
        
        when(credentialRepo.save(any(PlatformCredential.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        when(credentialRepo.existsById(anyLong())).thenReturn(true);

        // Act
        PlatformCredential result = credentialService.mergeOrUpdateCredential(newCred, label);

        // Assert
        assertNotNull(result);
        assertEquals(existingCred1.getId(), result.getId());
        
        verify(credentialRepo).deleteById(newCred.getId());
        verify(credentialRepo).deleteById(existingCred2.getId());
        verify(credentialRepo, never()).deleteById(existingCred1.getId());
    }

    @Test
    void testMergeOrUpdateCredential_NullLabel_DoesNotUpdateLabel() {
        // Arrange
        PlatformCredential newCred = SellerNexusTestDataBuilder.createTestCredential();
        newCred.setId(5L);
        newCred.setSeller(testUser);
        newCred.setLabel("Original Label");
        
        when(credentialRepo.findAllByExternalMerchantIdAndPlatform(
                newCred.getExternalMerchantId(), "JOOM"))
                .thenReturn(Collections.emptyList());

        // Act
        PlatformCredential result = credentialService.mergeOrUpdateCredential(newCred, null);

        // Assert
        assertNotNull(result);
        assertEquals("Original Label", result.getLabel());
        verify(credentialRepo, never()).save(any());
    }

    @Test
    void testMergeOrUpdateCredential_EmptyLabel_DoesNotUpdateLabel() {
        // Arrange
        PlatformCredential newCred = SellerNexusTestDataBuilder.createTestCredential();
        newCred.setId(5L);
        newCred.setSeller(testUser);
        newCred.setLabel("Original Label");
        
        when(credentialRepo.findAllByExternalMerchantIdAndPlatform(
                newCred.getExternalMerchantId(), "JOOM"))
                .thenReturn(Collections.emptyList());

        // Act
        PlatformCredential result = credentialService.mergeOrUpdateCredential(newCred, "");

        // Assert
        assertNotNull(result);
        assertEquals("Original Label", result.getLabel());
        verify(credentialRepo, never()).save(any());
    }

    @Test
    void testMergeOrUpdateCredential_NullSeller_UpdatesLabelOnly() {
        // Arrange
        String label = "No Seller Label";
        PlatformCredential newCred = SellerNexusTestDataBuilder.createTestCredential();
        newCred.setId(5L);
        newCred.setSeller(null);
        
        when(credentialRepo.save(any(PlatformCredential.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PlatformCredential result = credentialService.mergeOrUpdateCredential(newCred, label);

        // Assert
        assertNotNull(result);
        assertEquals(label, result.getLabel());
        verify(credentialRepo).save(newCred);
        verify(credentialRepo, never()).findAllByExternalMerchantIdAndPlatform(any(), any());
    }
}
