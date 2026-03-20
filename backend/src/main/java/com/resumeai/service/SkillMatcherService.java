package com.resumeai.service;

import com.resumeai.dto.AnalysisResponse.SkillResult;
import com.resumeai.model.Skill;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SkillMatcherService {

    private static final Logger log = LoggerFactory.getLogger(SkillMatcherService.class);
    private static final double JARO_WINKLER_THRESHOLD = 0.92;
    private static final int LEVENSHTEIN_MAX_DISTANCE = 2;

    private final JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
    private final LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();
    private final DataLoaderService dataLoader;

    public SkillMatcherService(DataLoaderService dataLoader) {
        this.dataLoader = dataLoader;
    }

    /**
     * Extract all recognizable skills from a block of text.
     * Uses three strategies: exact match, synonym match, fuzzy match.
     */
    public List<SkillResult> extractSkillsFromText(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();

        String normalizedText = normalizeText(text);
        Map<String, SkillResult> found = new LinkedHashMap<>();

        Map<String, Skill> skillByName = dataLoader.getSkillByNameLower();
        Map<String, String> synonymMap = dataLoader.getSynonymToCanonical();

        // Strategy 1 & 2: Exact match + synonym match (regex word-boundary)
        for (Map.Entry<String, String> entry : synonymMap.entrySet()) {
            String token = entry.getKey();
            String canonical = entry.getValue();

            if (found.containsKey(canonical.toLowerCase())) continue;

            // Build pattern: word-boundary aware, case-insensitive
            String escaped = Pattern.quote(token);
            Pattern pattern = Pattern.compile(
                "(?i)(?<![a-zA-Z0-9+#])(" + escaped + ")(?![a-zA-Z0-9+#])"
            );

            if (pattern.matcher(normalizedText).find()) {
                Skill skill = skillByName.get(canonical.toLowerCase());
                if (skill == null) {
                    // find by canonical name
                    skill = findSkillByName(canonical);
                }
                if (skill != null) {
                    boolean isExact = token.equalsIgnoreCase(skill.getName());
                    found.put(canonical.toLowerCase(), SkillResult.builder()
                        .name(skill.getName())
                        .category(skill.getCategory())
                        .weight(skill.getWeight())
                        .matchType(isExact ? "EXACT" : "SYNONYM")
                        .build());
                }
            }
        }

        // Strategy 3: Fuzzy matching on individual tokens and bigrams
        List<String> tokens = tokenizeText(normalizedText);
        List<String> candidates = buildCandidates(tokens);

        for (String candidate : candidates) {
            if (candidate.length() < 2) continue;
            for (Skill skill : dataLoader.getAllSkills()) {
                String skillLower = skill.getName().toLowerCase();
                if (found.containsKey(skillLower)) continue;

                // Jaro-Winkler similarity
                double similarity = jaroWinkler.apply(candidate, skillLower);
                if (similarity >= JARO_WINKLER_THRESHOLD) {
                    found.put(skillLower, SkillResult.builder()
                        .name(skill.getName())
                        .category(skill.getCategory())
                        .weight(skill.getWeight())
                        .matchType("FUZZY")
                        .build());
                    continue;
                }

                // Levenshtein distance for short tokens
                if (candidate.length() >= 4 && skillLower.length() >= 4) {
                    Integer distance = levenshtein.apply(candidate, skillLower);
                    if (distance != null && distance <= LEVENSHTEIN_MAX_DISTANCE
                            && Math.abs(candidate.length() - skillLower.length()) <= 1) {
                        found.put(skillLower, SkillResult.builder()
                            .name(skill.getName())
                            .category(skill.getCategory())
                            .weight(skill.getWeight())
                            .matchType("FUZZY")
                            .build());
                    }
                }
            }
        }

        List<SkillResult> results = new ArrayList<>(found.values());
        results.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));
        log.debug("Extracted {} skills from text ({} chars)", results.size(), text.length());
        return results;
    }

    /**
     * Compute matching between two skill sets.
     * Returns matched (in both) and missing (in JD but not resume).
     */
    public MatchResult computeMatch(List<SkillResult> resumeSkills, List<SkillResult> jdSkills) {
        Set<String> resumeNames = resumeSkills.stream()
            .map(s -> s.getName().toLowerCase())
            .collect(Collectors.toSet());

        List<SkillResult> matched = new ArrayList<>();
        List<SkillResult> missing = new ArrayList<>();

        for (SkillResult jdSkill : jdSkills) {
            if (resumeNames.contains(jdSkill.getName().toLowerCase())) {
                matched.add(jdSkill);
            } else {
                missing.add(jdSkill);
            }
        }

        matched.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));
        missing.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));

        return new MatchResult(matched, missing);
    }

    private String normalizeText(String text) {
        return text
            .replaceAll("[\\r\\n\\t]+", " ")
            .replaceAll("[^\\w\\s.#+/\\-@]", " ")
            .replaceAll("\\s+", " ")
            .toLowerCase()
            .trim();
    }

    private List<String> tokenizeText(String text) {
        return Arrays.stream(text.split("[\\s,;|•·]+"))
            .map(String::trim)
            .filter(t -> !t.isEmpty())
            .collect(Collectors.toList());
    }

    private List<String> buildCandidates(List<String> tokens) {
        List<String> candidates = new ArrayList<>(tokens);
        // Add bigrams
        for (int i = 0; i < tokens.size() - 1; i++) {
            candidates.add(tokens.get(i) + " " + tokens.get(i + 1));
        }
        // Add trigrams
        for (int i = 0; i < tokens.size() - 2; i++) {
            candidates.add(tokens.get(i) + " " + tokens.get(i + 1) + " " + tokens.get(i + 2));
        }
        return candidates;
    }

    private Skill findSkillByName(String name) {
        String lower = name.toLowerCase();
        return dataLoader.getAllSkills().stream()
            .filter(s -> s.getName().equalsIgnoreCase(lower))
            .findFirst().orElse(null);
    }

    public record MatchResult(List<SkillResult> matched, List<SkillResult> missing) {}
}
