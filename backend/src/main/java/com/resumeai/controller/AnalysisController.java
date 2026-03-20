package com.resumeai.controller;

import com.resumeai.dto.AnalysisResponse;
import com.resumeai.service.AnalysisService;
import com.resumeai.service.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);

    private final AnalysisService analysisService;
    private final DataLoaderService dataLoader;

    public AnalysisController(AnalysisService analysisService, DataLoaderService dataLoader) {
        this.analysisService = analysisService;
        this.dataLoader = dataLoader;
    }

    /**
     * Main analysis endpoint.
     * Accepts multipart/form-data with:
     *   resumeFile  - uploaded resume (PDF/DOCX/TXT) OR
     *   resumeText  - pasted resume text
     *   jdText      - job description text OR
     *   jdUrl       - job posting URL to scrape
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyze(
            @RequestPart(value = "resumeFile", required = false) MultipartFile resumeFile,
            @RequestParam(value = "resumeText", required = false) String resumeText,
            @RequestParam(value = "jdText", required = false) String jdText,
            @RequestParam(value = "jdUrl", required = false) String jdUrl) {

        try {
            // Validation
            boolean hasResume = (resumeFile != null && !resumeFile.isEmpty())
                || (resumeText != null && !resumeText.isBlank());
            boolean hasJd = (jdText != null && !jdText.isBlank())
                || (jdUrl != null && !jdUrl.isBlank());

            if (!hasResume) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please provide a resume file or paste resume text."));
            }
            if (!hasJd) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please provide a job description or URL."));
            }

            AnalysisResponse response = analysisService.analyze(resumeFile, resumeText, jdText, jdUrl);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Analysis failed", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Analysis failed: " + e.getMessage()));
        }
    }

    /** Health check */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "skillsLoaded", dataLoader.getAllSkills().size(),
            "jobFieldsLoaded", dataLoader.getAllJobFields().size()
        ));
    }

    /** List available skills */
    @GetMapping("/skills")
    public ResponseEntity<?> getSkills(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "50") int limit) {
        var skills = dataLoader.getAllSkills();
        if (category != null && !category.isBlank()) {
            skills = skills.stream()
                .filter(s -> category.equalsIgnoreCase(s.getCategory()))
                .toList();
        }
        return ResponseEntity.ok(Map.of(
            "total", skills.size(),
            "skills", skills.stream().limit(limit).toList()
        ));
    }

    /** List job fields */
    @GetMapping("/job-fields")
    public ResponseEntity<?> getJobFields(@RequestParam(required = false) String domain) {
        var fields = dataLoader.getAllJobFields();
        if (domain != null && !domain.isBlank()) {
            fields = fields.stream()
                .filter(f -> domain.equalsIgnoreCase(f.getDomain()))
                .toList();
        }
        return ResponseEntity.ok(Map.of("total", fields.size(), "jobFields", fields));
    }
}
