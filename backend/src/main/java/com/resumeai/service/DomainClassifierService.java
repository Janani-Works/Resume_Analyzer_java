package com.resumeai.service;

import com.resumeai.dto.AnalysisResponse.SkillResult;
import com.resumeai.model.JobField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DomainClassifierService {

    private static final Logger log = LoggerFactory.getLogger(DomainClassifierService.class);

    private final DataLoaderService dataLoader;

    public DomainClassifierService(DataLoaderService dataLoader) {
        this.dataLoader = dataLoader;
    }

    /**
     * Classify text to a job field using scored matching.
     * Returns the best matching JobField.
     */
    public ClassificationResult classify(String text, List<SkillResult> skills) {
        if (text == null || text.isBlank()) {
            return new ClassificationResult("Software Engineer", "Engineering", "Low", 0);
        }

        String textLower = text.toLowerCase();
        Set<String> skillNames = new HashSet<>();
        for (SkillResult s : skills) {
            skillNames.add(s.getName().toLowerCase());
        }

        Map<String, Integer> scores = new LinkedHashMap<>();
        JobField bestField = null;
        int bestScore = -1;

        for (JobField field : dataLoader.getAllJobFields()) {
            int score = 0;

            // Title mention in text
            if (textLower.contains(field.getTitle().toLowerCase())) {
                score += 30;
            }
            // Domain mention
            if (textLower.contains(field.getDomain().toLowerCase())) {
                score += 10;
            }
            // Key skills overlap
            if (field.getKeySkills() != null) {
                for (String ks : field.getKeySkills()) {
                    if (skillNames.contains(ks.toLowerCase()) || textLower.contains(ks.toLowerCase())) {
                        score += 5;
                    }
                }
            }

            if (score > bestScore) {
                bestScore = score;
                bestField = field;
            }
        }

        if (bestField == null) {
            return new ClassificationResult("Software Engineer", "Engineering", "Low", 0);
        }

        String domainMatch;
        if (bestScore >= 50) domainMatch = "High";
        else if (bestScore >= 20) domainMatch = "Medium";
        else domainMatch = "Low";

        log.debug("Classified as '{}' with score {}", bestField.getTitle(), bestScore);
        return new ClassificationResult(bestField.getTitle(), bestField.getDomain(), domainMatch, bestScore);
    }

    public record ClassificationResult(String jobTitle, String domain, String domainMatch, int score) {}
}
