package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 스킬 업데이트 요청 DTO
 */
public class SkillUpdate {
    @SerializedName("skill_ids")
    private List<Integer> skillIds;
    
    @SerializedName("custom_skills")
    private List<String> customSkills;
    
    public SkillUpdate(List<Integer> skillIds, List<String> customSkills) {
        this.skillIds = skillIds;
        this.customSkills = customSkills;
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
}
