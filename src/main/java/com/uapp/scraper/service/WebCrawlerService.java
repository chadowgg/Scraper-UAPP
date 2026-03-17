package com.uapp.scraper.service;

import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

public interface WebCrawlerService {
    void crawl(String baseUrl, String currentUrl, ExecutorService executorService, Phaser phaser);
    void processImages(Document document, String pageUrl);
    boolean isInternalLink(String href, String baseUrl);
    void start();
    void stop();
}
