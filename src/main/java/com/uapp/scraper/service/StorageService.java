package com.uapp.scraper.service;

public interface StorageService {
    void saveImage(String originalUrl,
                   Long originalSize,
                   byte[] compressedData,
                   String pageUrl);
    String saveToFileSystem(String originalUrl, byte[] data);
}
