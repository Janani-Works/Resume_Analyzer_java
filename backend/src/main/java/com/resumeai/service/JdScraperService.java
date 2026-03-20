package com.resumeai.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JdScraperService {

    private static final Logger log = LoggerFactory.getLogger(JdScraperService.class);

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/120.0.0.0 Safari/537.36";

    /**
     * Scrape job description text from a URL.
     * Removes scripts, styles, nav, header, footer, then extracts main content.
     */
    public String scrapeFromUrl(String url) {
        if (url == null || url.isBlank()) return "";
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(15_000)
                    .followRedirects(true)
                    .get();

            // Remove boilerplate
            doc.select("script, style, nav, header, footer, aside, .nav, .footer, .header").remove();

            // Try to find main content container
            String[] contentSelectors = {
                "main", "article", "[class*='job-description']", "[class*='description']",
                "[class*='job-detail']", "[class*='content']", "[id*='job']",
                "[id*='description']", ".job-content", "#main-content", "body"
            };

            for (String sel : contentSelectors) {
                Element el = doc.selectFirst(sel);
                if (el != null) {
                    String text = el.text();
                    if (text.length() > 200) {
                        log.info("JD scraped via selector '{}': {} chars", sel, text.length());
                        return text;
                    }
                }
            }

            // Final fallback
            return Jsoup.clean(doc.body().html(), Safelist.none()).replaceAll("\\s+", " ").trim();

        } catch (Exception e) {
            log.warn("Failed to scrape URL {}: {}", url, e.getMessage());
            return "";
        }
    }
}
