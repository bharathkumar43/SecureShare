package com.secureshare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "file_shares")
public class FileShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "share_id", unique = true, nullable = false)
    private String shareId;

    @Column(name = "source_cloud")
    private String sourceCloud;

    @Column(name = "destination_cloud")
    private String destinationCloud;

    @Column(name = "to_email")
    private String toEmail;

    @Column(name = "from_email")
    private String fromEmail;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "expiry")
    private LocalDateTime expiry;

    @Column(name = "download_limit")
    private int downloadLimit;

    @Column(name = "downloads_used")
    private int downloadsUsed = 0;

    @Column(name = "watermark")
    private boolean watermark;

    @Column(name = "destination_link", length = 1000)
    private String destinationLink;

    @Column(name = "status")
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "share_files", joinColumns = @JoinColumn(name = "share_id"))
    @Column(name = "file_path")
    private List<String> filePaths;

    @Column(name = "file_count")
    private int fileCount;

    // Constructors
    public FileShare() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getSourceCloud() {
        return sourceCloud;
    }

    public void setSourceCloud(String sourceCloud) {
        this.sourceCloud = sourceCloud;
    }

    public String getDestinationCloud() {
        return destinationCloud;
    }

    public void setDestinationCloud(String destinationCloud) {
        this.destinationCloud = destinationCloud;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }

    public int getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(int downloadLimit) {
        this.downloadLimit = downloadLimit;
    }

    public int getDownloadsUsed() {
        return downloadsUsed;
    }

    public void setDownloadsUsed(int downloadsUsed) {
        this.downloadsUsed = downloadsUsed;
    }

    public boolean isWatermark() {
        return watermark;
    }

    public void setWatermark(boolean watermark) {
        this.watermark = watermark;
    }

    public String getDestinationLink() {
        return destinationLink;
    }

    public void setDestinationLink(String destinationLink) {
        this.destinationLink = destinationLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    // Helper methods
    public int getRemainingDownloads() {
        if ("unlimited".equals(String.valueOf(downloadLimit))) {
            return Integer.MAX_VALUE;
        }
        return Math.max(0, downloadLimit - downloadsUsed);
    }

    public boolean isExpired() {
        return expiry != null && LocalDateTime.now().isAfter(expiry);
    }

    public boolean isActive() {
        return "ACTIVE".equals(status) && !isExpired() && getRemainingDownloads() > 0;
    }
}