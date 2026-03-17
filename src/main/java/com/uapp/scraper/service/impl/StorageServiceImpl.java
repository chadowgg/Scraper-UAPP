package com.uapp.scraper.service.impl;

import com.uapp.scraper.entity.Image;
import com.uapp.scraper.repository.ImageRepository;
import com.uapp.scraper.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final ImageRepository imageRepository;

    @Value("${storage.path}")
    private String storagePath;

    @Override
    public void saveImage(String originalUrl, Long originalSize, byte[] compressedData, String pageUrl) {
        log.info("Saving image");

        if (imageRepository.existsByOriginalUrl(originalUrl)) {
            log.info("Image already exists!");
            return;
        }

        String filePath = saveToFileSystem(originalUrl, compressedData);

        Image image = Image.builder()
                .originalUrl(originalUrl)
                .originalSize(originalSize)
                .compressedSize((long) compressedData.length)
                .compressedFilePath(filePath)
                .pageUrl(pageUrl)
                .build();

        imageRepository.save(image);

        log.info("Image saved: {}", originalUrl);
    }

    @Override
    public String saveToFileSystem(String originalUrl, byte[] data) {
        log.info("Save image to filesystem");
        try {
            Path directory = Paths.get(storagePath);
            Files.createDirectories(directory);

            String fileName = generateFileName(originalUrl);
            Path filePath = directory.resolve(fileName);

            Files.write(filePath, data);

            return filePath.toString();
        } catch (IOException e) {
            log.error("Failed to save file: {}", originalUrl, e);
            throw new RuntimeException("Failed to save image to filesystem", e);
        }
    }

    private String getExtension(String url) {
        if (url.contains(".png")) return ".png";
        if (url.contains(".webp")) return ".webp";
        return ".jpg";
    }

    private String generateFileName(String url) {
        String extension = getExtension(url);
        return UUID.randomUUID() + extension;
    }
}
