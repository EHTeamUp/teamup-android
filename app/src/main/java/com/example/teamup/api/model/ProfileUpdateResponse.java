package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 프로필 업데이트 응답 DTO
 */
public class ProfileUpdateResponse {
    @SerializedName("message")
    private String message;
    
    @SerializedName("updated_skills")
    private UserSkillsResponse updatedSkills;
    
    @SerializedName("updated_roles")
    private UserRolesResponse updatedRoles;
    
    @SerializedName("updated_experiences_count")
    private Integer updatedExperiencesCount;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public UserSkillsResponse getUpdatedSkills() {
        return updatedSkills;
    }
    
    public void setUpdatedSkills(UserSkillsResponse updatedSkills) {
        this.updatedSkills = updatedSkills;
    }
    
    public UserRolesResponse getUpdatedRoles() {
        return updatedRoles;
    }
    
    public void setUpdatedRoles(UserRolesResponse updatedRoles) {
        this.updatedRoles = updatedRoles;
    }
    
    public Integer getUpdatedExperiencesCount() {
        return updatedExperiencesCount;
    }
    
    public void setUpdatedExperiencesCount(Integer updatedExperiencesCount) {
        this.updatedExperiencesCount = updatedExperiencesCount;
    }
}
