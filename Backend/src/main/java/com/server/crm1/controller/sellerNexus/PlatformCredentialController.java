package com.server.crm1.controller.sellerNexus;

import com.server.crm1.model.sellurNexus.PlatformCredential;
import com.server.crm1.service.sellerNexus.PlatformCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credential")
@RequiredArgsConstructor
public class PlatformCredentialController {

    private final PlatformCredentialService service;

    @PostMapping("/add")
    public PlatformCredential add(@RequestBody PlatformCredential credential) {
        return service.save(credential);
    }
}

