package com.resumeai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillsDatabase {
    private String version;
    private int total;
    private List<Skill> skills;
}
