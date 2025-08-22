package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 회원가입 2단계 DTO
 */
public class RegistrationStep2 {
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("skill_ids")
    private List<Integer> skillIds;
    
    @SerializedName("custom_skills")
    private List<String> customSkills;
    
    @SerializedName("role_ids")
    private List<Integer> roleIds;
    
    @SerializedName("custom_roles")
    private List<String> customRoles;
    
    public RegistrationStep2(String userId, List<Integer> skillIds, List<String> customSkills, List<Integer> roleIds, List<String> customRoles) {
        this.userId = userId;
        this.skillIds = skillIds;
        this.customSkills = customSkills;
        this.roleIds = roleIds;
        this.customRoles = customRoles;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public List<Integer> getSkillIds() {
        return skillIds;
    }
    
    public void setSkillIds(List<Integer> skillIds) {
        this.skillIds = skillIds;
    }
    
    public List<String> getCustomSkills() {
        return customSkills;
    }
    
    public void setCustomSkills(List<String> customSkills) {
        this.customSkills = customSkills;
    }
    
    public List<Integer> getRoleIds() {
        return roleIds;
    }
    
    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
    
    public List<String> getCustomRoles() {
        return customRoles;
    }
    
    public void setCustomRoles(List<String> customRoles) {
        this.customRoles = customRoles;
    }
}
