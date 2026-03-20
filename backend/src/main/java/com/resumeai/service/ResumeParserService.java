package com.resumeai.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ResumeParserService {

    private static final Logger log = LoggerFactory.getLogger(ResumeParserService.class);

    /**
     * Extract text from an uploaded resume file.
     * Supports PDF (.pdf), DOCX (.docx, .doc), and plain text.
     */
    public String extractText(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return "";
        }
        String filename = (file.getOriginalFilename() != null)
                ? file.getOriginalFilename().toLowerCase()
                : "";

        if (filename.endsWith(".pdf")) {
            return extractFromPdf(file.getBytes());
        } else if (filename.endsWith(".docx") || filename.endsWith(".doc")) {
            return extractFromDocx(file.getInputStream());
        } else {
            // Treat as plain text (txt, rtf fallback)
            return new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    private String extractFromPdf(byte[] bytes) {
        try {
            try (PDDocument document = Loader.loadPDF(bytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                return stripper.getText(document);
            }
        } catch (IOException e) {
            log.warn("PDF extraction failed: {}", e.getMessage());
            return "";
        }
    }

    private String extractFromDocx(InputStream inputStream) {
        try {
            try (XWPFDocument document = new XWPFDocument(inputStream);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                return extractor.getText();
            }
        } catch (IOException e) {
            log.warn("DOCX extraction failed: {}", e.getMessage());
            return "";
        }
    }
}
