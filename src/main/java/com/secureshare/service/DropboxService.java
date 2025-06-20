package com.secureshare.service;

import org.springframework.stereotype.Service;

@Service
public class DropboxService {
    public String uploadFile() {
        // TODO: Implement OAuth2-based Dropbox file upload
        return "https://www.dropbox.com/s/SAMPLE_DROPBOX_ID/file.txt?dl=0";
    }
}
