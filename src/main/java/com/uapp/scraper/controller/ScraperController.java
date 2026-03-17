package com.uapp.scraper.controller;

import com.uapp.scraper.service.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/scraper")
@Slf4j
@RequiredArgsConstructor
public class ScraperController {

    private final ScraperService scraperService;

    @PostMapping("/start")
    public ResponseEntity<String> startScraper(@RequestParam String url) {
        log.info("Received scraping request for: {}", url);

        if (url == null || url.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid URL");
        }

        CompletableFuture.runAsync(() -> scraperService.scrape(url));

        return ResponseEntity.ok().body("Scraping started for: " + url);
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopScraper() {
        log.info("Received scraping request for stop");

        scraperService.stopScrape();

        return ResponseEntity.ok().body("Scraping stopped");
    }
}
