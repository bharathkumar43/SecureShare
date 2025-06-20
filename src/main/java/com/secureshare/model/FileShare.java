package com.secureshare.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class FileShare {

    @Id
    @GeneratedValue
    private Long id;

    private String sourceCloud;
    private String destinationCloud;
    private String toEmail;
    private String fromEmail;
    private String password;  // Encrypted password
    private String expiry;
    private int downloadLimit;
    private boolean watermark;
    private String link;
    private String status;    // "Active" or "Revoked"

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public int getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(int downloadLimit) {
        this.downloadLimit = downloadLimit;
    }

    public boolean isWatermark() {
        return watermark;
    }

    public void setWatermark(boolean watermark) {
        this.watermark = watermark;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
