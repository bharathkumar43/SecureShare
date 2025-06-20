package com.secureshare.service;

import com.secureshare.model.FileShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Removed the circular dependency on FileShareService

    public void sendLinkWithPassword(FileShare share, String rawPassword) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(share.getToEmail());
        msg.setSubject("Your Secure File Link");
        msg.setText(
            "Download Link: " + share.getLink() + "\n" +
            "Password: " + rawPassword + "\n" +
            "Expiry: " + share.getExpiry() + "\n\n" +
            "This link is valid until the expiry date or until revoked."
        );
        mailSender.send(msg);
    }
}
