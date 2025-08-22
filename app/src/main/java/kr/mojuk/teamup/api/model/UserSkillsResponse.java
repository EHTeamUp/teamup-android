package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 스킬 조회 응답 DTO
 */
public class UserSkillsResponse {
    @SerializedName("skill_ids")
    private List<Integer> skillIds;
    
    @SerializedName("custom_skills")
    private List<String> customSkills;
    
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
}
