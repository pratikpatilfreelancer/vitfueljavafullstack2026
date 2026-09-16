package com.pockettrack.service.impl;

import com.pockettrack.service.BillScanService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

@Service
public class BillScanServiceImpl implements BillScanService {

    private static final Pattern TOTAL_PATTERN = Pattern.compile(
        "(?i)(?:total|amount|grand\\s*total|net\\s*amount)\\s*[:=\\-]?\\s*(?:rs\\.?|inr)?\\s*([0-9]+(?:[,.][0-9]{1,2})?)"
    );

    private final Tesseract tesseract;

    public BillScanServiceImpl() {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("tessdata");
        this.tesseract.setLanguage("eng");
    }

    @Override
    public BigDecimal scanTotal(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Bill image is required.");
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

            if (bufferedImage == null) {
                throw new IllegalArgumentException("Invalid bill image.");
            }

            String extractedText = tesseract.doOCR(bufferedImage);
            return extractAmountFromText(extractedText);

        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read bill image.", e);
        } catch (TesseractException e) {
            throw new IllegalArgumentException("Unable to scan bill image using OCR.", e);
        }
    }

    private BigDecimal extractAmountFromText(String text) {

        String normalizedText = text.replace("\r", "\n");

        String[] patterns = {
            "(?i)grand\\s*total\\s*[:=\\-]?\\s*(?:₹|rs\\.?|inr)?\\s*([0-9]{1,3}(?:,[0-9]{3})+(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)\\b",
            
            "(?i)(?<!grand\\s)total\\s*[:=\\-]?\\s*(?:₹|rs\\.?|inr)?\\s*([0-9]{1,3}(?:,[0-9]{3})+(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)\\b",
            
            "(?i)net\\s*amount\\s*[:=\\-]?\\s*(?:₹|rs\\.?|inr)?\\s*([0-9]{1,3}(?:,[0-9]{3})+(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)\\b",
            
            "(?i)amount\\s*[:=\\-]\\s*(?:₹|rs\\.?|inr)?\\s*([0-9]{1,3}(?:,[0-9]{3})+(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)\\b"
        };

        for (String pattern : patterns) {

            java.util.regex.Pattern p =
                    java.util.regex.Pattern.compile(pattern);

            java.util.regex.Matcher matcher =
                    p.matcher(normalizedText);

            if (matcher.find()) {

                String amountText = matcher.group(1);

                amountText = amountText.replace(",", "");

                return new BigDecimal(amountText);
            }
        }

        throw new IllegalArgumentException(
                "Could not find total amount in the bill."
        );
    }
}
