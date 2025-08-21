package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class PersonalityProfileResponse {
    @SerializedName("profile_code")
    private String profileCode;
    
    @SerializedName("traits_json")
    private PersonalityTraits traitsJson;
    
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("user_id")
    private String userId;
    
    public PersonalityProfileResponse() {}
    
    public PersonalityProfileResponse(String profileCode, PersonalityTraits traitsJson, Integer id, String userId) {
        this.profileCode = profileCode;
        this.traitsJson = traitsJson;
        this.id = id;
        this.userId = userId;
    }
    
    public String getProfileCode() {
        return profileCode;
    }
    
    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }
    
    public PersonalityTraits getTraitsJson() {
        return traitsJson;
    }
    
    public void setTraitsJson(PersonalityTraits traitsJson) {
        this.traitsJson = traitsJson;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "PersonalityProfileResponse{" +
                "profileCode='" + profileCode + '\'' +
                ", traitsJson=" + traitsJson +
                ", id=" + id +
                ", userId='" + userId + '\'' +
                '}';
    }
}
