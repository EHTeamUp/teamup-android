package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class PersonalityTestResponse {
    @SerializedName("profile_code")
    private String profileCode;
    
    @SerializedName("display_name")
    private String displayName;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("traits")
    private Map<String, String> traits;
    
    @SerializedName("completed_at")
    private String completedAt;
    
    public String getProfileCode() {
        return profileCode;
    }
    
    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, String> getTraits() {
        return traits;
    }
    
    public void setTraits(Map<String, String> traits) {
        this.traits = traits;
    }
    
    public String getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}
