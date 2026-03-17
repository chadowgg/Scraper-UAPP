package com.uapp.scraper.service.impl;

import com.uapp.scraper.service.ScraperService;
import com.uapp.scraper.service.WebCrawlerService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScraperServiceImpl implements ScraperService {

    private final WebCrawlerService webCrawlerService;

    @Value("${threads.pool}")
    private int threadCount;

    @Override
    public void scrape(String url) {
        Phaser phaser = new Phaser(1);
        webCrawlerService.start();
        try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            log.info("Starting scraping url: {}", url);

            webCrawlerService.crawl(url, url, executorService, phaser);

            phaser.arriveAndAwaitAdvance();
        } finally {
            log.info("Finished scraping url: {}", url);
        }
    }

    @Override
    public void stopScrape() {
        log.info("Stopping scraping");
        webCrawlerService.stop();
    }
}
