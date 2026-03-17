package com.uapp.scraper.repository;

import com.uapp.scraper.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    boolean existsByOriginalUrl(String originalUrl);
}
