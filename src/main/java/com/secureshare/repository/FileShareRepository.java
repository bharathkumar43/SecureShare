package com.secureshare.repository;

import com.secureshare.model.FileShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileShareRepository extends JpaRepository<FileShare, Long> {
    Optional<FileShare> findByShareId(String shareId);
    Optional<FileShare> findByDestinationLink(String destinationLink);
    List<FileShare> findByExpiryBeforeAndStatus(LocalDateTime expiry, String status);
    List<FileShare> findByFromEmailOrderByCreatedAtDesc(String fromEmail);
    List<FileShare> findByToEmailOrderByCreatedAtDesc(String toEmail);
}