package com.secureshare.controller;

import com.secureshare.model.FileShare;
import com.secureshare.service.FileShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ShareController {

    @Autowired
    private FileShareService service;

    @PostMapping("/share")
    public ResponseEntity<Map<String, String>> shareFile(@RequestBody FileShare share) throws Exception {
        String link = service.generateLink(share);
        return ResponseEntity.ok(Map.of("link", link));
    }

    @PostMapping("/revoke")
    public ResponseEntity<String> revokeLink(@RequestBody Map<String, String> req) {
        service.revokeLink(req.get("link"));
        return ResponseEntity.ok("Link revoked");
    }
}
