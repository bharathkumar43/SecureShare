package com.secureshare.service;

import com.secureshare.model.FileShare;
import com.secureshare.repository.FileShareRepository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class FileShareService {
    @Autowired private FileShareRepository repo;
    @Autowired private EmailService emailService;
    @Autowired private GoogleDriveService googleDriveService;
    @Autowired private DropboxService dropboxService;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    public String generateLink(FileShare share) throws Exception {
        String destinationUrl = "";
        if ("Google Drive".equals(share.getDestinationCloud())) {
            destinationUrl = googleDriveService.uploadFile();
        } else if ("Dropbox".equals(share.getDestinationCloud())) {
            destinationUrl = dropboxService.uploadFile();
        }

        String uniqueLink = "https://secureshare.com/download/" + UUID.randomUUID();
        share.setLink(uniqueLink);
        String rawPassword = share.getPassword();
        String encrypted = passwordEncoder.encode(rawPassword);

        share.setLink(destinationUrl);
        share.setPassword(encrypted);
        share.setStatus("ACTIVE");
        repo.save(share);

        emailService.sendLinkWithPassword(share, rawPassword);
        return destinationUrl;
    }

    public void revokeLink(String link) {
        repo.findByLink(link).ifPresent(share -> {
            share.setStatus("REVOKED");
            repo.save(share);
        });
    }
}
