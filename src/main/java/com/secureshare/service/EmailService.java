package com.secureshare.service;

import com.secureshare.model.FileShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendShareNotification(FileShare share, String shareLink, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(share.getToEmail());
            helper.setFrom(share.getFromEmail());
            helper.setSubject("🔒 Secure File Share - " + share.getFileCount() + " file(s) shared with you");

            String htmlContent = createEmailTemplate(share, shareLink, password);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String createEmailTemplate(FileShare share, String shareLink, String password) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
        String formattedExpiry = share.getExpiry().format(formatter);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Secure File Share</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f5f5f5; }
                    .container { max-width: 600px; margin: 0 auto; background-color: white; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; }
                    .download-box { background: #f8f9fa; border-left: 4px solid #667eea; padding: 20px; margin: 20px 0; border-radius: 5px; }
                    .password-box { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 15px 0; }
                    .button { display: inline-block; background: linear-gradient(45deg, #667eea, #764ba2); color: white; padding: 15px 30px; text-decoration: none; border-radius: 25px; font-weight: bold; margin: 20px 0; }
                    .footer { background: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin: 20px 0; }
                    .info-item { background: #f8f9fa; padding: 15px; border-radius: 8px; }
                    .info-label { font-weight: bold; color: #333; font-size: 12px; text-transform: uppercase; }
                    .info-value { color: #667eea; font-size: 16px; margin-top: 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔒 Secure File Share</h1>
                        <p>You have received secure files from %s</p>
                    </div>
                    
                    <div class="content">
                        <h2>Hello!</h2>
                        <p><strong>%s</strong> has securely shared <strong>%d file(s)</strong> with you through CloudFuze SecureShare.</p>
                        
                        <div class="download-box">
                            <h3>📁 Download Details</h3>
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">Files Count</div>
                                    <div class="info-value">%d file(s)</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Download Limit</div>
                                    <div class="info-value">%s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Expires On</div>
                                    <div class="info-value">%s</div>
                                </div>
                                <div class="info-item">
                                    <div class="info-label">Destination</div>
                                    <div class="info-value">%s</div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="password-box">
                            <h4>🔑 Access Password</h4>
                            <p>You will need this password to access the files:</p>
                            <code style="background: white; padding: 10px; border-radius: 5px; font-size: 16px; font-weight: bold; color: #e74c3c;">%s</code>
                        </div>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">🔗 Access Secure Files</a>
                        </div>
                        
                        <div style="background: #e8f4fd; padding: 20px; border-radius: 8px; margin: 20px 0;">
                            <h4>📋 Instructions:</h4>
                            <ol>
                                <li>Click the "Access Secure Files" button above</li>
                                <li>Enter the provided password when prompted</li>
                                <li>Download your files to %s</li>
                            </ol>
                        </div>
                        
                        <div style="background: #fff2f2; border: 1px solid #ffcccb; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <h4>⚠️ Important Security Notes:</h4>
                            <ul>
                                <li>This link will expire on <strong>%s</strong></li>
                                <li>Limited to <strong>%s download(s)</strong></li>
                                <li>Keep your password secure and don't share it</li>
                                <li>Download files promptly before expiration</li>
                            </ul>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>🛡️ This email was sent by CloudFuze SecureShare</p>
                        <p>Secure • Fast • Reliable File Sharing</p>
                        <p style="margin-top: 15px; font-size: 10px;">
                            If you did not expect this email, please ignore it. 
                            This link will automatically expire after the specified time.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                share.getFromEmail(),
                share.getFromEmail(),
                share.getFileCount(),
                share.getFileCount(),
                share.getDownloadLimit() == Integer.MAX_VALUE ? "Unlimited" : String.valueOf(share.getDownloadLimit()),
                formattedExpiry,
                share.getDestinationCloud(),
                password,
                shareLink,
                share.getDestinationCloud(),
                formattedExpiry,
                share.getDownloadLimit() == Integer.MAX_VALUE ? "unlimited" : String.valueOf(share.getDownloadLimit())
            );
    }
}