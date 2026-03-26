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
@CrossOrigin(origins = "*") // 🔥 IMPORTANT (change later to your frontend URL)
public class AnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);

    private final AnalysisService analysisService;
    private final DataLoaderService dataLoader;

    public AnalysisController(AnalysisService analysisService, DataLoaderService dataLoader) {
        this.analysisService = analysisService;
        this.dataLoader = dataLoader;
    }

    @PostMapping(value = "/analyze")
    public ResponseEntity<?> analyze(

            // ✅ FIX: use @RequestPart consistently for multipart
            @RequestPart(value = "resumeFile", required = false) MultipartFile resumeFile,

            @RequestPart(value = "resumeText", required = false) String resumeText,
            @RequestPart(value = "jdText", required = false) String jdText,
            @RequestPart(value = "jdUrl", required = false) String jdUrl
    ) {

        try {
            // Validation
            boolean hasResume = (resumeFile != null && !resumeFile.isEmpty())
                    || (resumeText != null && !resumeText.isBlank());

            boolean hasJd = (jdText != null && !jdText.isBlank())
                    || (jdUrl != null && !jdUrl.isBlank());

            if (!hasResume) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please provide a resume file or resume text."));
            }

            if (!hasJd) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please provide a job description or URL."));
            }

            AnalysisResponse response =
                    analysisService.analyze(resumeFile, resumeText, jdText, jdUrl);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Analysis failed", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Analysis failed"));
        }
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "skillsLoaded", dataLoader.getAllSkills().size(),
                "jobFieldsLoaded", dataLoader.getAllJobFields().size()
        ));
    }
}