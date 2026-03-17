package com.uapp.scraper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", unique = true, nullable = false)
    private String originalUrl;

    @Column(name = "original_size")
    private Long originalSize;

    @Column(name = "compressed_size")
    private Long compressedSize;

    @Column(name = "compressed_file_path")
    private String compressedFilePath;

    @Column(name = "page_url")
    private String pageUrl;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
