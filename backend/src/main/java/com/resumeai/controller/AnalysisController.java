package com.resumeai.controller;

import com.resumeai.dto.AnalysisResponse;
import com.resumeai.service.AnalysisService;
import com.resumeai.service.DataLoaderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Change this to your frontend URL later
public class AnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);

    private final AnalysisService analysisService;
    private final DataLoaderService dataLoader;

    public AnalysisController(AnalysisService analysisService, DataLoaderService dataLoader) {
        this.analysisService = analysisService;
        this.dataLoader = dataLoader;
    }

    // 🔍 Resume Analysis Endpoint
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(

            @RequestPart(value = "resumeFile", required = false) MultipartFile resumeFile,
            @RequestPart(value = "resumeText", required = false) String resumeText,
            @RequestPart(value = "jdText", required = false) String jdText,
            @RequestPart(value = "jdUrl", required = false) String jdUrl
    ) {

        try {
            // ✅ Validate inputs
            boolean hasResume =
                    (resumeFile != null && !resumeFile.isEmpty()) ||
                            (resumeText != null && !resumeText.isBlank());

            boolean hasJd =
                    (jdText != null && !jdText.isBlank()) ||
                            (jdUrl != null && !jdUrl.isBlank());

            if (!hasResume) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Provide resume file or resume text"));
            }

            if (!hasJd) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Provide job description or job URL"));
            }

            // 🚀 Call service
            AnalysisResponse response =
                    analysisService.analyze(resumeFile, resumeText, jdText, jdUrl);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error during analysis", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error"));
        }
    }

    // ❤️ Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "skillsLoaded", dataLoader.getAllSkills().size(),
                "jobFieldsLoaded", dataLoader.getAllJobFields().size()
        ));
    }

    // 🧪 Debug endpoint (VERY IMPORTANT for Railway)
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}