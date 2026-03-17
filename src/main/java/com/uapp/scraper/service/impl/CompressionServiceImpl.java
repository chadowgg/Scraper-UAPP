package com.uapp.scraper.service.impl;

import com.uapp.scraper.service.CompressionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class CompressionServiceImpl implements CompressionService {
    @Override
    public byte[] compress(byte[] imageData, String contentType) {
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));

            if (originalImage == null) {
                log.error("Failed to read image");
                return null;
            }

            int newWidth = originalImage.getWidth() / 2;
            int newHeight = originalImage.getHeight() / 2;

            String format = getFormat(contentType);

            BufferedImage compressedImage = new BufferedImage(newWidth, newHeight, getType(format));

            Graphics2D g2d = compressedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(compressedImage, format, outputStream);

            log.info("Compressed image from {}kb to {}kb",
                    imageData.length / 1024,
                    outputStream.toByteArray().length / 1024);

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Image compression error: {}", e.getMessage());
            return null;
        }
    }


    private String getFormat(String contentType) {
        if (contentType == null) return "jpg";

        String type = contentType.toLowerCase();

        if (type.contains("png")) return "png";
        if (type.contains("webp")) return "webp";
        return "jpg";
    }

    private int getType(String format) {
        if (format.equals("png") || format.equals("webp")) {
            return BufferedImage.TYPE_INT_ARGB;
        }
        return BufferedImage.TYPE_INT_RGB;
    }
}
