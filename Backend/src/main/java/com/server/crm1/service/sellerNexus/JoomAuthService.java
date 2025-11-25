package com.server.crm1.service.sellerNexus;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.server.crm1.model.sellurNexus.PlatformCredential;
import com.server.crm1.model.sellurNexus.Seller;
import com.server.crm1.model.users.User;
import com.server.crm1.repository.sellerNexus.PlatformCredentialRepository;
import com.server.crm1.repository.sellerNexus.SellerRepository;
import com.server.crm1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JoomAuthService {

    private final PlatformCredentialRepository credentialRepo;
    private final RestTemplate restTemplate;
    private final SellerRepository sellerRepo;
    private final UserService userService;


    private final String redirectUri = "http://localhost:8080/api/joom/auth/callback";

    @Value("${app.auth.tokenSecret}")
    private String tokenSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getAuthorizationUrl(String clientId, Integer userId, String label) {
        try {
            // build a small JSON payload for state
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("uid", userId);
            payload.put("cid", clientId);
            if (label != null) payload.put("label", label);
            payload.put("ts", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(payload);
            String encoded = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // sign
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(tokenSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] sig = mac.doFinal(encoded.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String sigB64 = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(sig);

            String state = encoded + "." + sigB64;

            return "https://api-merchant.joom.com/api/v2/oauth/authorize"
                    + "?client_id=" + clientId
                    + "&redirect_uri=" + redirectUri
                    + "&response_type=code"+ "&prompt=login"
                    + "&state=" + state;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to build authorization URL", ex);
        }
    }

    // In-memory transient store mapping state -> clientSecret with expiry
    private static final java.util.concurrent.ConcurrentHashMap<String, java.util.Map<String, Object>> stateSecretMap = new java.util.concurrent.ConcurrentHashMap<>();

    public void storeSecretForState(String state, String clientSecret) {
        if (state == null || clientSecret == null) return;
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        m.put("secret", clientSecret);
        m.put("expiry", System.currentTimeMillis() + 5 * 60 * 1000); // 5 minutes
        stateSecretMap.put(state, m);
    }

    public String retrieveAndRemoveSecretForState(String state) {
        if (state == null) return null;
        java.util.Map<String, Object> m = stateSecretMap.remove(state);
        if (m == null) return null;
        Long expiry = (Long) m.get("expiry");
        if (expiry != null && expiry < System.currentTimeMillis()) return null;
        return (String) m.get("secret");
    }


    public PlatformCredential exchangeCodeForToken( User seller,
            String clientId, String clientSecret, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<java.util.Map> response = restTemplate.postForEntity(
            "https://api-merchant.joom.com/api/v2/oauth/access_token", request, java.util.Map.class);

        java.util.Map responseBodyRaw = response.getBody();
        if (responseBodyRaw == null) {
            throw new RuntimeException("Invalid token response from Joom: null response");
        }

        java.util.Map<String, Object> data = (java.util.Map<String, Object>) responseBodyRaw.get("data");
        if (data == null) {
            throw new RuntimeException("Invalid token response from Joom: 'data' field is missing");
        }


        // Extract fields from Joom response
        String accessToken = (String) data.get("access_token");
        String refreshToken = (String) data.get("refresh_token");
        Integer expiresIn = (Integer) data.get("expires_in");
        Integer expiryTime = (Integer) data.get("expiry_time");
        String merchantUserId = (String) data.get("merchant_user_id");

//        System.out.println("User:-"+seller.getName());
//
        System.out.println("Access Token:-"+accessToken +"\nRefresh Token:-"+refreshToken+"\nExpires In:-"+expiresIn+"\nExpiry Time:-"+expiryTime+"\nMerchant User Id:-"+merchantUserId);

        // Using BUILDER DESIGN PATTERN for cleaner object creation
        // Instead of multiple setter calls, we use a fluent builder interface
        PlatformCredential credential = PlatformCredential.builder()
                .seller(seller)
                .platform("JOOM")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .expiryTime(expiryTime.longValue())
                .externalMerchantId(merchantUserId)
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .build();

        return credentialRepo.save(credential);
    }

    /**
     * Parse and verify the signed state produced by getAuthorizationUrl
     * Returns the payload map (uid, label, ts) if valid, otherwise throws
     */
    public java.util.Map<String, Object> parseState(String state) {
        try {
            if (state == null || !state.contains(".")) {
                throw new RuntimeException("Invalid state");
            }
            String[] parts = state.split("\\.");
            String encoded = parts[0];
            String sig = parts[1];

            // verify
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(tokenSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] expected = mac.doFinal(encoded.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String expectedB64 = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(expected);
            if (!java.security.MessageDigest.isEqual(expectedB64.getBytes(java.nio.charset.StandardCharsets.UTF_8), sig.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                throw new RuntimeException("Invalid state signature");
            }

            byte[] jsonBytes = java.util.Base64.getUrlDecoder().decode(encoded);
            String json = new String(jsonBytes, java.nio.charset.StandardCharsets.UTF_8);
            java.util.Map<String, Object> map = objectMapper.readValue(json, java.util.Map.class);
            return map;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse state", ex);
        }
    }

    public void testAccessToken(String accessToken) {
        String url = "https://api-merchant.joom.com/api/v2/auth_test";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        System.out.println("AUTH TEST RESPONSE: " + response.getBody());
    }

    public PlatformCredential refreshAccessToken(PlatformCredential creds) {

        String url = "https://api-merchant.joom.com/api/v2/oauth/refresh_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        System.out.println("refresh token:-"+creds.getRefreshToken()+"\n");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "31594d8557863747");
        body.add("client_secret", "a3c764ecd75d2bd5e5675a0e2ef4a217");
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", creds.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map data = (Map) response.getBody().get("data");
        String accessToken = (String) data.get("access_token");
        String refreshToken = (String) data.get("refresh_token");
        Integer expiresIn = (Integer) data.get("expires_in");
        Long expiryTime = ((Number) data.get("expiry_time")).longValue();

        System.out.println("Access Token new:-"+accessToken +"\nRefresh Token new:-"+refreshToken+"\nExpires In new:-"+expiresIn+"\nExpiry Time:-"+expiryTime);

        // Update credential
        creds.setAccessToken(accessToken);
        creds.setRefreshToken(refreshToken);
        creds.setExpiresIn(expiresIn);
        creds.setExpiryTime(expiryTime);
//
//        // Save in DB
        credentialRepo.save(creds);

        return creds;
    }



}

