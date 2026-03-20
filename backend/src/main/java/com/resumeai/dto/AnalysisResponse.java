package com.resumeai.dto;

import com.resumeai.model.Skill;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnalysisResponse {
    private int matchScore;
    private int atsScore;
    private String domainMatch;
    private String jobField;
    private String jobDomain;
    private List<SkillResult> matchedSkills;
    private List<SkillResult> missingSkills;
    private List<SkillResult> resumeSkills;
    private List<SkillResult> jdSkills;
    private List<String> suggestions;
    private double keywordDensity;
    private String analysisId;
    private long processingTimeMs;

    @Data
    @Builder
    public static class SkillResult {
        private String name;
        private String category;
        private int weight;
        private String matchType; // EXACT, SYNONYM, FUZZY
    }
}
