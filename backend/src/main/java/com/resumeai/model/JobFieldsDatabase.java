package com.resumeai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobFieldsDatabase {
    private String version;
    private int total;
    @JsonProperty("job_fields")
    private List<JobField> jobFields;
}
