package com.secureshare.model;

public class ShareRequest {
    private String sourceCloud;
    private String destinationCloud;
    private String toEmail;
    private String fromEmail;
    private boolean watermark;
    private String password;
    private String expiry;
    private String downloadLimit;

    // Getters and Setters
    public String getSourceCloud() { return sourceCloud; }
    public void setSourceCloud(String sourceCloud) { this.sourceCloud = sourceCloud; }
    public String getDestinationCloud() { return destinationCloud; }
    public void setDestinationCloud(String destinationCloud) { this.destinationCloud = destinationCloud; }
    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }
    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
    public boolean isWatermark() { return watermark; }
    public void setWatermark(boolean watermark) { this.watermark = watermark; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getExpiry() { return expiry; }
    public void setExpiry(String expiry) { this.expiry = expiry; }
    public String getDownloadLimit() { return downloadLimit; }
    public void setDownloadLimit(String downloadLimit) { this.downloadLimit = downloadLimit; }
}
