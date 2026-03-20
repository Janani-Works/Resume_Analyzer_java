package com.resumeai.service;

import com.resumeai.dto.AnalysisResponse;
import com.resumeai.dto.AnalysisResponse.SkillResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final ResumeParserService resumeParser;
    private final JdScraperService jdScraper;
    private final SkillMatcherService skillMatcher;
    private final DomainClassifierService domainClassifier;
    private final ScoringService scoringService;

    public AnalysisService(ResumeParserService resumeParser,
                           JdScraperService jdScraper,
                           SkillMatcherService skillMatcher,
                           DomainClassifierService domainClassifier,
                           ScoringService scoringService) {
        this.resumeParser = resumeParser;
        this.jdScraper = jdScraper;
        this.skillMatcher = skillMatcher;
        this.domainClassifier = domainClassifier;
        this.scoringService = scoringService;
    }

    /**
     * Full analysis pipeline:
     * 1. Extract resume text (file or plain text)
     * 2. Get JD text (raw or scraped from URL)
     * 3. Extract skills from both
     * 4. Match skills
     * 5. Classify domain
     * 6. Score
     * 7. Generate suggestions
     */
    public AnalysisResponse analyze(MultipartFile resumeFile, String resumeText,
                                     String jdText, String jdUrl) throws IOException {
        long startTime = System.currentTimeMillis();
        String analysisId = UUID.randomUUID().toString().substring(0, 12);
        log.info("[{}] Starting analysis", analysisId);

        // 1. Get resume text
        String finalResumeText;
        if (resumeFile != null && !resumeFile.isEmpty()) {
            finalResumeText = resumeParser.extractText(resumeFile);
            log.info("[{}] Extracted {} chars from file: {}", analysisId,
                finalResumeText.length(), resumeFile.getOriginalFilename());
        } else {
            finalResumeText = resumeText != null ? resumeText : "";
        }

        if (finalResumeText.isBlank()) {
            throw new IllegalArgumentException("Resume text could not be extracted. Please provide a valid file or text.");
        }

        // 2. Get JD text
        String finalJdText;
        if (jdUrl != null && !jdUrl.isBlank()) {
            finalJdText = jdScraper.scrapeFromUrl(jdUrl.trim());
            if (finalJdText.isBlank() && jdText != null && !jdText.isBlank()) {
                finalJdText = jdText; // fallback to provided text
            }
            log.info("[{}] Scraped {} chars from URL", analysisId, finalJdText.length());
        } else {
            finalJdText = jdText != null ? jdText : "";
        }

        if (finalJdText.isBlank()) {
            throw new IllegalArgumentException("Job description could not be obtained. Please provide text or a valid URL.");
        }

        // 3. Extract skills
        List<SkillResult> resumeSkills = skillMatcher.extractSkillsFromText(finalResumeText);
        List<SkillResult> jdSkills = skillMatcher.extractSkillsFromText(finalJdText);
        log.info("[{}] Resume: {} skills, JD: {} skills", analysisId, resumeSkills.size(), jdSkills.size());

        // 4. Match
        SkillMatcherService.MatchResult matchResult = skillMatcher.computeMatch(resumeSkills, jdSkills);

        // 5. Classify domain
        DomainClassifierService.ClassificationResult classification =
            domainClassifier.classify(finalJdText, jdSkills);

        // 6. Score
        int matchScore = scoringService.calculateMatchScore(matchResult.matched().size(), jdSkills.size());
        int atsScore = scoringService.calculateAtsScore(
            finalResumeText, finalJdText, matchResult.matched(), jdSkills.size());
        double keywordDensity = scoringService.calculateKeywordDensity(finalResumeText, finalJdText);

        // 7. Suggestions
        List<String> suggestions = scoringService.generateSuggestions(
            matchResult.missing(), finalResumeText, matchScore, atsScore);

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("[{}] Analysis complete in {}ms. Match: {}%, ATS: {}", analysisId, elapsed, matchScore, atsScore);

        return AnalysisResponse.builder()
            .matchScore(matchScore)
            .atsScore(atsScore)
            .domainMatch(classification.domainMatch())
            .jobField(classification.jobTitle())
            .jobDomain(classification.domain())
            .matchedSkills(matchResult.matched())
            .missingSkills(matchResult.missing())
            .resumeSkills(resumeSkills)
            .jdSkills(jdSkills)
            .suggestions(suggestions)
            .keywordDensity(keywordDensity)
            .analysisId(analysisId)
            .processingTimeMs(elapsed)
            .build();
    }
}
