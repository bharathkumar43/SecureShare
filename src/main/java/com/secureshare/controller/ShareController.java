package com.secureshare.controller;

import com.secureshare.model.FileShare;
import com.secureshare.service.FileShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ShareController {

    @Autowired
    private FileShareService fileShareService;

    @PostMapping("/share")
    public ResponseEntity<?> shareFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("sourceCloud") String sourceCloud,
            @RequestParam("destinationCloud") String destinationCloud,
            @RequestParam("toEmail") String toEmail,
            @RequestParam("fromEmail") String fromEmail,
            @RequestParam("password") String password,
            @RequestParam("expiry") String expiry,
            @RequestParam("downloadLimit") String downloadLimit,
            @RequestParam(value = "watermark", defaultValue = "false") boolean watermark) {
        
        try {
            String shareLink = fileShareService.createShare(
                files, sourceCloud, destinationCloud, toEmail, fromEmail,
                password, expiry, downloadLimit, watermark
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "link", shareLink,
                "message", "Files shared successfully and email sent!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error sharing files: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/share-json")
    public ResponseEntity<?> shareFilesJson(@RequestBody Map<String, Object> request) {
        try {
            String shareLink = fileShareService.createShareFromJson(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "link", shareLink,
                "message", "Share created successfully and email sent!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error creating share: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/share/{shareId}")
    public ResponseEntity<?> getShareInfo(@PathVariable String shareId) {
        try {
            FileShare share = fileShareService.getShareInfo(shareId);
            
            return ResponseEntity.ok(Map.of(
                "shareId", share.getShareId(),
                "fromEmail", share.getFromEmail(),
                "fileCount", share.getFileCount(),
                "expiry", share.getExpiry(),
                "remainingDownloads", share.getRemainingDownloads(),
                "isActive", share.isActive()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        try {
            String shareId = request.get("shareId");
            String password = request.get("password");
            
            boolean isValid = fileShareService.verifyPassword(shareId, password);
            
            if (isValid) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password verified successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid password"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/download/{shareId}")
    public ResponseEntity<?> downloadFiles(
            @PathVariable String shareId,
            @RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            String downloadUrl = fileShareService.processDownload(shareId, password);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "downloadUrl", downloadUrl,
                "message", "Download initiated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revokeLink(@RequestBody Map<String, String> request) {
        try {
            String link = request.get("link");
            fileShareService.revokeShare(link);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Link revoked successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/auth/{provider}")
    public ResponseEntity<?> getAuthUrl(@PathVariable String provider) {
        try {
            String authUrl = fileShareService.getAuthUrl(provider);
            return ResponseEntity.ok(Map.of(
                "authUrl", authUrl,
                "provider", provider
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error getting auth URL: " + e.getMessage()
            ));
        }
    }
}