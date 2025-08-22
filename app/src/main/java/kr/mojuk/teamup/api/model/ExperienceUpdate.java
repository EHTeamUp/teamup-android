package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 경험 업데이트 요청 DTO
 */
public class ExperienceUpdate {
    @SerializedName("experiences")
    private List<Experience> experiences;
    
    public ExperienceUpdate(List<Experience> experiences) {
        this.experiences = experiences;
    }
    
    public List<Experience> getExperiences() {
        return experiences;
    }
    
    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }
}
