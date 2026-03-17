package com.uapp.scraper.service;

public interface CompressionService {
    byte[] compress(byte[] imageData, String contentType);
}
