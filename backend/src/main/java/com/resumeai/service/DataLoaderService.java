package com.resumeai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeai.model.JobField;
import com.resumeai.model.JobFieldsDatabase;
import com.resumeai.model.Skill;
import com.resumeai.model.SkillsDatabase;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class DataLoaderService {

    private static final Logger log = LoggerFactory.getLogger(DataLoaderService.class);

    private List<Skill> skills = new ArrayList<>();
    private List<JobField> jobFields = new ArrayList<>();

    // Maps for fast lookup
    private final Map<String, Skill> skillByNameLower = new HashMap<>();
    private final Map<String, String> synonymToCanonical = new HashMap<>(); // synonym -> canonical skill name

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void loadData() {
        loadSkills();
        loadJobFields();
        log.info("Loaded {} skills and {} job fields", skills.size(), jobFields.size());
    }

    private void loadSkills() {
        try {
            InputStream is = new ClassPathResource("skills.json").getInputStream();
            SkillsDatabase db = objectMapper.readValue(is, SkillsDatabase.class);
            if (db != null && db.getSkills() != null) {
                skills = db.getSkills();
                for (Skill s : skills) {
                    String nameLower = s.getName().toLowerCase().trim();
                    skillByNameLower.put(nameLower, s);
                    synonymToCanonical.put(nameLower, s.getName());
                    if (s.getSynonyms() != null) {
                        for (String syn : s.getSynonyms()) {
                            synonymToCanonical.put(syn.toLowerCase().trim(), s.getName());
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to load skills.json: {}", e.getMessage());
        }
    }

    private void loadJobFields() {
        try {
            InputStream is = new ClassPathResource("job_fields.json").getInputStream();
            JobFieldsDatabase db = objectMapper.readValue(is, JobFieldsDatabase.class);
            if (db != null && db.getJobFields() != null) {
                jobFields = db.getJobFields();
            }
        } catch (IOException e) {
            log.error("Failed to load job_fields.json: {}", e.getMessage());
        }
    }

    public List<Skill> getAllSkills() { return Collections.unmodifiableList(skills); }
    public List<JobField> getAllJobFields() { return Collections.unmodifiableList(jobFields); }
    public Map<String, Skill> getSkillByNameLower() { return Collections.unmodifiableMap(skillByNameLower); }
    public Map<String, String> getSynonymToCanonical() { return Collections.unmodifiableMap(synonymToCanonical); }
}
