package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 스킬 정보 DTO
 */
public class Skill {
    @SerializedName("skill_id")
    private int skillId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("is_custom")
    private boolean isCustom;
    
    public int getSkillId() {
        return skillId;
    }
    
    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isCustom() {
        return isCustom;
    }
    
    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
