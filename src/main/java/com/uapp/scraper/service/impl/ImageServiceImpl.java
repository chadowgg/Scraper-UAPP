package com.uapp.scraper.service.impl;

import com.uapp.scraper.service.CompressionService;
import com.uapp.scraper.service.ImageService;
import com.uapp.scraper.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final StorageService storageService;
    private final CompressionService compressionService;

    private final static int ONE_MEMORY_BLOCK = 4096;
    private final static int END_READ_BUFFER = -1;
    private final static int MIN_SIZE_BYTES = 200 * 1024;

    @Override
    public void processImage(String imageUrl, String pageUrl) {
        Path tempFile = null;

        try {
            log.info("Processing image: {}", imageUrl);
            HttpURLConnection connection = openConnection(imageUrl);
            byte[] imageData = downloadImage(connection);
            String contentType = connection.getContentType();

            if (imageData.length < MIN_SIZE_BYTES) {
                log.info("Image too small, skipping: {}", imageUrl);
                return;
            }

            tempFile = Files.createTempFile("scraper_temp_file_", ".tmp");
            Files.write(tempFile, imageData);

            log.info("Processing image: {} | kb: {}", imageUrl, imageData.length);

            byte[] compressedImage = compressionService.compress(Files.readAllBytes(tempFile), contentType);

            if (compressedImage == null) {
                log.error("Failed to compress image: {}", imageUrl);
                return;
            }

            storageService.saveImage(imageUrl, (long) imageData.length, compressedImage, pageUrl);
        } catch (IOException e) {
            log.error("Failed to process image: {}", imageUrl, e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.delete(tempFile);
                } catch (IOException e) {
                    log.warn("Failed to delete temp file: {}", tempFile, e);
                }
            }
        }
    }

    @Override
    public HttpURLConnection openConnection(String imageUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        log.info("HttpURLConnection: {}", connection);
        return connection;
    }

    @Override
    public byte[] downloadImage(HttpURLConnection connection) throws IOException {
        log.info("Downloading image from: {}", connection);
        int status = connection.getResponseCode();
        if (status >= 200 && status < 300) {
            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[ONE_MEMORY_BLOCK];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != END_READ_BUFFER) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            }
        }
        throw new IOException("HTTP error: " + status);
    }
}
