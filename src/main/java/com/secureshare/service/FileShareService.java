package com.secureshare.service;

import com.secureshare.model.FileShare;
import com.secureshare.repository.FileShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FileShareService {

    @Autowired
    private FileShareRepository fileShareRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private GoogleDriveService googleDriveService;

    @Autowired
    private DropboxService dropboxService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String createShare(MultipartFile[] files, String sourceCloud, String destinationCloud,
                            String toEmail, String fromEmail, String password, String expiry,
                            String downloadLimit, boolean watermark) throws Exception {
        
        // Generate unique share ID
        String shareId = UUID.randomUUID().toString();
        
        // Create FileShare entity
        FileShare fileShare = new FileShare();
        fileShare.setShareId(shareId);
        fileShare.setSourceCloud(sourceCloud);
        fileShare.setDestinationCloud(destinationCloud);
        fileShare.setToEmail(toEmail);
        fileShare.setFromEmail(fromEmail);
        fileShare.setPasswordHash(passwordEncoder.encode(password));
        fileShare.setExpiry(LocalDateTime.parse(expiry, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        fileShare.setDownloadLimit("unlimited".equals(downloadLimit) ? Integer.MAX_VALUE : Integer.parseInt(downloadLimit));
        fileShare.setWatermark(watermark);
        fileShare.setFileCount(files.length);
        fileShare.setStatus("ACTIVE");

        // Upload files to destination cloud
        String destinationLink = uploadFilesToCloud(files, destinationCloud, shareId);
        fileShare.setDestinationLink(destinationLink);

        // Save to database
        fileShareRepository.save(fileShare);

        // Send email notification
        String shareLink = "http://localhost:5173/download/" + shareId;
        emailService.sendShareNotification(fileShare, shareLink, password);

        return shareLink;
    }

    public String createShareFromJson(Map<String, Object> request) throws Exception {
        // This method handles JSON requests from the frontend
        String shareId = UUID.randomUUID().toString();
        
        FileShare fileShare = new FileShare();
        fileShare.setShareId(shareId);
        fileShare.setSourceCloud((String) request.get("sourceCloud"));
        fileShare.setDestinationCloud((String) request.get("destinationCloud"));
        fileShare.setToEmail((String) request.get("toEmail"));
        fileShare.setFromEmail((String) request.get("fromEmail"));
        fileShare.setPasswordHash(passwordEncoder.encode((String) request.get("password")));
        
        String expiryStr = (String) request.get("expiry");
        fileShare.setExpiry(LocalDateTime.parse(expiryStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        String downloadLimitStr = (String) request.get("downloadLimit");
        fileShare.setDownloadLimit("unlimited".equals(downloadLimitStr) ? Integer.MAX_VALUE : Integer.parseInt(downloadLimitStr));
        
        fileShare.setWatermark((Boolean) request.getOrDefault("watermark", false));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> selectedFiles = (List<Map<String, Object>>) request.get("selectedFiles");
        fileShare.setFileCount(selectedFiles != null ? selectedFiles.size() : 0);
        fileShare.setStatus("ACTIVE");

        // For demo purposes, create a mock destination link
        String destinationLink = createMockDestinationLink(fileShare.getDestinationCloud(), shareId);
        fileShare.setDestinationLink(destinationLink);

        // Save to database
        fileShareRepository.save(fileShare);

        // Send email notification
        String shareLink = "http://localhost:5173/download/" + shareId;
        emailService.sendShareNotification(fileShare, shareLink, (String) request.get("password"));

        return shareLink;
    }

    private String uploadFilesToCloud(MultipartFile[] files, String cloudProvider, String shareId) throws Exception {
        switch (cloudProvider.toLowerCase()) {
            case "google drive":
                return googleDriveService.uploadFiles(files, shareId);
            case "dropbox":
                return dropboxService.uploadFiles(files, shareId);
            default:
                throw new IllegalArgumentException("Unsupported cloud provider: " + cloudProvider);
        }
    }

    private String createMockDestinationLink(String cloudProvider, String shareId) {
        // Create mock links for demo purposes
        switch (cloudProvider.toLowerCase()) {
            case "google drive":
                return "https://drive.google.com/drive/folders/" + shareId + "?usp=sharing";
            case "dropbox":
                return "https://www.dropbox.com/sh/" + shareId + "/shared-folder?dl=0";
            default:
                return "https://cloud-storage.example.com/shared/" + shareId;
        }
    }

    public FileShare getShareInfo(String shareId) throws Exception {
        Optional<FileShare> shareOpt = fileShareRepository.findByShareId(shareId);
        if (shareOpt.isEmpty()) {
            throw new Exception("Share not found or expired");
        }
        
        FileShare share = shareOpt.get();
        if (!share.isActive()) {
            throw new Exception("Share is no longer active");
        }
        
        return share;
    }

    public boolean verifyPassword(String shareId, String password) throws Exception {
        FileShare share = getShareInfo(shareId);
        return passwordEncoder.matches(password, share.getPasswordHash());
    }

    public String processDownload(String shareId, String password) throws Exception {
        FileShare share = getShareInfo(shareId);
        
        // Verify password
        if (!passwordEncoder.matches(password, share.getPasswordHash())) {
            throw new Exception("Invalid password");
        }
        
        // Check download limits
        if (share.getRemainingDownloads() <= 0) {
            throw new Exception("Download limit exceeded");
        }
        
        // Increment download count
        share.setDownloadsUsed(share.getDownloadsUsed() + 1);
        fileShareRepository.save(share);
        
        // Return the destination link for download
        return share.getDestinationLink();
    }

    public void revokeShare(String shareLink) throws Exception {
        // Extract shareId from link
        String shareId = shareLink.substring(shareLink.lastIndexOf("/") + 1);
        
        Optional<FileShare> shareOpt = fileShareRepository.findByShareId(shareId);
        if (shareOpt.isPresent()) {
            FileShare share = shareOpt.get();
            share.setStatus("REVOKED");
            fileShareRepository.save(share);
        } else {
            throw new Exception("Share not found");
        }
    }

    public String getAuthUrl(String provider) throws Exception {
        switch (provider.toLowerCase()) {
            case "googledrive":
                return googleDriveService.getAuthUrl();
            case "dropbox":
                return dropboxService.getAuthUrl();
            default:
                throw new Exception("Unsupported provider: " + provider);
        }
    }

    // Cleanup expired shares (can be called by a scheduled task)
    public void cleanupExpiredShares() {
        List<FileShare> expiredShares = fileShareRepository.findByExpiryBeforeAndStatus(
            LocalDateTime.now(), "ACTIVE"
        );
        
        for (FileShare share : expiredShares) {
            share.setStatus("EXPIRED");
            fileShareRepository.save(share);
        }
    }
}