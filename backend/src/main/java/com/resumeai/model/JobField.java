package com.resumeai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobField {
    private int id;
    private String title;
    private String domain;
    private String level;
    @JsonProperty("key_skills")
    private List<String> keySkills;
    private int weight;
}
