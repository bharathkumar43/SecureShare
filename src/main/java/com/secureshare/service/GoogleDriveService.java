package com.secureshare.service;

import org.springframework.stereotype.Service;

@Service
public class GoogleDriveService {
    public String uploadFile() {
        // TODO: Implement OAuth2-based Google Drive upload
        // Return publicly shareable link upon success
        return "https://drive.google.com/file/d/SAMPLE_DRIVE_ID/view?usp=sharing";
    }
}
