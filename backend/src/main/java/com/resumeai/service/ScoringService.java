package com.resumeai.service;

import com.resumeai.dto.AnalysisResponse.SkillResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScoringService {

    private static final List<String> RESUME_SECTIONS =
        List.of("experience", "education", "skills", "projects", "summary",
                "objective", "certifications", "achievements", "work history");

    private static final List<String> ACTION_VERBS =
        List.of("built", "designed", "developed", "led", "managed", "created",
                "improved", "implemented", "architected", "reduced", "increased",
                "delivered", "launched", "scaled", "optimized", "automated",
                "collaborated", "mentored", "drove", "established", "transformed",
                "engineered", "deployed", "migrated", "integrated", "streamlined");

    private static final Pattern QUANTIFIER_PATTERN =
        Pattern.compile("\\d+\\s*(%|\\+|x|k|m|bn|years?|months?|teams?|engineers?|projects?|\\$)",
            Pattern.CASE_INSENSITIVE);

    /**
     * Calculate ATS score (0-100) from multiple signal components.
     */
    public int calculateAtsScore(String resumeText, String jdText,
                                  List<SkillResult> matchedSkills, int totalJdSkills) {
        int score = 0;

        // 1. Skill match ratio → 40 pts
        if (totalJdSkills > 0) {
            double ratio = (double) matchedSkills.size() / totalJdSkills;
            score += (int) Math.round(ratio * 40);
        }

        // 2. Keyword density (JD vs resume overlap) → 20 pts
        Set<String> jdWords = extractMeaningfulWords(jdText);
        Set<String> resumeWords = extractMeaningfulWords(resumeText);
        Set<String> intersection = new HashSet<>(jdWords);
        intersection.retainAll(resumeWords);
        if (!jdWords.isEmpty()) {
            double density = (double) intersection.size() / jdWords.size();
            score += (int) Math.min(density * 40, 20);
        }

        // 3. Resume structure sections → 20 pts
        String resumeLower = resumeText.toLowerCase();
        long sectionsFound = RESUME_SECTIONS.stream()
            .filter(resumeLower::contains)
            .count();
        score += (int) Math.round((double) sectionsFound / RESUME_SECTIONS.size() * 20);

        // 4. Quantified achievements → 10 pts
        Matcher matcher = QUANTIFIER_PATTERN.matcher(resumeText);
        int quantCount = 0;
        while (matcher.find()) quantCount++;
        score += Math.min(quantCount * 2, 10);

        // 5. Action verbs → 10 pts
        long verbCount = ACTION_VERBS.stream().filter(resumeLower::contains).count();
        score += (int) Math.min(verbCount, 10);

        return Math.min(score, 100);
    }

    /**
     * Calculate match score as percentage of JD skills found in resume.
     */
    public int calculateMatchScore(int matchedCount, int totalJdSkills) {
        if (totalJdSkills == 0) return 0;
        return (int) Math.round((double) matchedCount / totalJdSkills * 100);
    }

    /**
     * Calculate keyword density: what fraction of JD's meaningful keywords
     * appear in the resume text.
     */
    public double calculateKeywordDensity(String resumeText, String jdText) {
        Set<String> jdWords = extractMeaningfulWords(jdText);
        Set<String> resumeWords = extractMeaningfulWords(resumeText);
        if (jdWords.isEmpty()) return 0.0;
        Set<String> common = new HashSet<>(jdWords);
        common.retainAll(resumeWords);
        return Math.round((double) common.size() / jdWords.size() * 1000.0) / 10.0; // 1 decimal
    }

    /**
     * Generate targeted suggestions based on analysis results.
     */
    public List<String> generateSuggestions(List<SkillResult> missingSkills,
                                             String resumeText, int matchScore, int atsScore) {
        List<String> suggestions = new ArrayList<>();
        String resumeLower = resumeText.toLowerCase();

        // Missing top skills
        if (!missingSkills.isEmpty()) {
            List<String> top = missingSkills.stream()
                .limit(5)
                .map(SkillResult::getName)
                .toList();
            suggestions.add("🎯 Add these high-priority missing skills to your resume: " + String.join(", ", top));
        }

        // ATS score advice
        if (atsScore < 60) {
            suggestions.add("📋 Add a dedicated 'Skills' section listing technologies explicitly to improve ATS parsing.");
        }

        // Low match score
        if (matchScore < 50) {
            suggestions.add("🔑 Your profile matches less than 50% of the JD. Tailor your resume specifically for this role.");
        }

        // Missing quantification
        Matcher m = QUANTIFIER_PATTERN.matcher(resumeText);
        if (!m.find()) {
            suggestions.add("📊 Quantify your achievements — '50% performance improvement', 'led team of 8', '$2M revenue impact' significantly boost credibility.");
        }

        // Missing sections
        List<String> missingSections = RESUME_SECTIONS.stream()
            .limit(4)
            .filter(s -> !resumeLower.contains(s))
            .toList();
        if (!missingSections.isEmpty()) {
            suggestions.add("📝 Consider adding these standard sections: " + String.join(", ", missingSections));
        }

        // GitHub/portfolio
        if (!resumeLower.contains("github") && !resumeLower.contains("portfolio") && !resumeLower.contains("linkedin")) {
            suggestions.add("🔗 Add your GitHub profile or portfolio link — it significantly increases interview callbacks.");
        }

        // Category-specific advice
        boolean hasCloudGap = missingSkills.stream().anyMatch(s -> "Cloud".equals(s.getCategory()));
        boolean hasDevOpsGap = missingSkills.stream().anyMatch(s -> "DevOps".equals(s.getCategory()));
        boolean hasMlGap = missingSkills.stream().anyMatch(s -> "AI/ML".equals(s.getCategory()));

        if (hasCloudGap) suggestions.add("☁️ Cloud skills required. AWS/Azure/GCP certifications can close this gap quickly.");
        if (hasDevOpsGap) suggestions.add("🛠️ DevOps skills gap detected. Hands-on Docker/Kubernetes projects demonstrate these competencies.");
        if (hasMlGap) suggestions.add("🤖 AI/ML skills in demand for this role. Kaggle competitions or open-source contributions can showcase these.");

        // Positive feedback
        if (matchScore >= 80) {
            suggestions.add("🌟 Excellent match! Focus on customizing your summary statement to directly reflect the JD's language.");
        } else if (matchScore >= 60) {
            suggestions.add("✅ Good match! Bridge the remaining gap by highlighting projects demonstrating the missing skills.");
        }

        return suggestions.stream().limit(8).toList();
    }

    private Set<String> extractMeaningfulWords(String text) {
        if (text == null) return Set.of();
        Set<String> words = new HashSet<>();
        String[] tokens = text.toLowerCase().split("[\\s,;.\\-|•·()\\[\\]{}\"']+");
        for (String t : tokens) {
            if (t.length() >= 3 && !isStopWord(t)) {
                words.add(t);
            }
        }
        return words;
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of(
            "the", "and", "for", "are", "but", "not", "you", "all", "can", "her", "was",
            "one", "our", "out", "day", "get", "has", "him", "his", "how", "man", "new",
            "now", "old", "see", "two", "way", "who", "its", "did", "may", "such",
            "will", "with", "have", "this", "that", "they", "from", "been", "were"
        );
        return stopWords.contains(word);
    }
}
