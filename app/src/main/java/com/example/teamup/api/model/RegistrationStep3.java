package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 회원가입 3단계 DTO
 */
public class RegistrationStep3 {
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("experiences")
    private List<Experience> experiences;
    
    public RegistrationStep3(String userId, List<Experience> experiences) {
        this.userId = userId;
        this.experiences = experiences;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public List<Experience> getExperiences() {
        return experiences;
    }
    
    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }
}
