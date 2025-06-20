package com.secureshare.repository;

import com.secureshare.model.FileShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileShareRepository extends JpaRepository<FileShare, Long> {
    Optional<FileShare> findByLink(String link);
}
