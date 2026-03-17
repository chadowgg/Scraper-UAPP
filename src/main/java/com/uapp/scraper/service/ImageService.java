package com.uapp.scraper.service;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface ImageService {
    void processImage(String imageUrl, String pageUrl);
    HttpURLConnection openConnection(String imageUrl) throws IOException;
    byte[] downloadImage(HttpURLConnection connection) throws IOException;
}
