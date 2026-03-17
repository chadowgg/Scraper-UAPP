package com.uapp.scraper.service.impl;

import com.uapp.scraper.service.ImageService;
import com.uapp.scraper.service.WebCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebCrawlerServiceImpl implements WebCrawlerService {

    private final ImageService imageService;

    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void crawl(String baseUrl, String currentUrl, ExecutorService executorService, Phaser phaser) {
        if (!visitedUrls.add(currentUrl) || !running.get()) {
            phaser.arriveAndDeregister();
            return;
        }

        log.info("Crawling url: {}", currentUrl);
        phaser.register();

        try {
            Document document = Jsoup.connect(currentUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();
            document.setBaseUri(currentUrl);

            log.info("Document completed");
            processImages(document, currentUrl);

            Elements links = document.select("a[href]");
            for (Element link : links) {
                String linkUrl = link.absUrl("href");
                if (isInternalLink(linkUrl, baseUrl) && !visitedUrls.contains(linkUrl)) {
                    executorService.submit(() -> crawl(baseUrl, linkUrl, executorService, phaser));
                }
            }
        } catch (IOException e) {
            log.error("Failed to crawl url: {}", currentUrl, e);
        } finally {
            phaser.arriveAndDeregister();
        }
    }

    @Override
    public void processImages(Document document, String pageUrl) {
        log.info("Process images: {}", pageUrl);
        Elements images = document.select("img[src]");
        for (Element image : images) {
            String imageUrl = image.absUrl("src");

            if (!imageUrl.isEmpty()) {
                imageService.processImage(imageUrl, pageUrl);
            }
        }
        log.info("Finished process images: {}", pageUrl);
    }

    @Override
    public boolean isInternalLink(String linkUrl, String baseUrl) {
        return linkUrl.startsWith(baseUrl)
                && !linkUrl.contains("#")
                && !linkUrl.contains(".pdf")
                && !linkUrl.contains(".zip")
                && !linkUrl.endsWith(".jpg")
                && !linkUrl.endsWith(".jpeg")
                && !linkUrl.endsWith(".png")
                && !linkUrl.endsWith(".gif")
                && !linkUrl.endsWith(".webp");
    }

    @Override
    public void start() {
        running.set(true);
    }

    @Override
    public void stop() {
        running.set(false);
        visitedUrls.clear();
    }
}
