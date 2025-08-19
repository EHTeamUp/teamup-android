package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 경험 생성 요청 DTO (ExperienceUpdate와 동일하지만 명확성을 위해 분리)
 */
public class ExperienceCreate {
    @SerializedName("experiences")
    private List<Experience> experiences;
    
    public ExperienceCreate(List<Experience> experiences) {
        this.experiences = experiences;
    }
    
    public List<Experience> getExperiences() {
        return experiences;
    }
    
    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }
}
