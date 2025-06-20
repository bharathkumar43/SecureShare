package com.secureshare.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ShareService {
    public String generateSecureLink() {
        // Generate random UUID as mock secure link
        return "https://secure.cloudfuze.com/share/" + UUID.randomUUID().toString();
    }
}
