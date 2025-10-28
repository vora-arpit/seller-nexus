package com.startup.sellertransfer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransferIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testFullProductTransferFlow() throws Exception {
        // Step 1: Register Seller
        mockMvc.perform(post("/api/seller/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John\", \"email\": \"john@example.com\", \"password\": \"Password@123\"}"))
                .andExpect(status().isOk());

        // Step 2: Link Platform Credentials
        mockMvc.perform(post("/api/seller/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceKey\": \"SRC123\", \"destinationKey\": \"DEST456\"}"))
                .andExpect(status().isOk());

        // Step 3: Trigger Product Transfer
        mockMvc.perform(post("/api/transfer/start?sellerId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transfer successful"));
    }
}

