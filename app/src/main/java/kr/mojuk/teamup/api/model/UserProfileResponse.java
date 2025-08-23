package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserProfileResponse {
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("skills")
    private UserSkillsResponse skills;
    
    @SerializedName("roles")
    private UserRolesResponse roles;
    
    @SerializedName("experiences")
    private List<Experience> experiences;
    
    @SerializedName("personality_profile")
    private PersonalityProfileResponse personalityProfile;

    public UserProfileResponse(String userId, UserSkillsResponse skills, UserRolesResponse roles, 
                             List<Experience> experiences, PersonalityProfileResponse personalityProfile) {
        this.userId = userId;
        this.skills = skills;
        this.roles = roles;
        this.experiences = experiences;
        this.personalityProfile = personalityProfile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserSkillsResponse getSkills() {
        return skills;
    }

    public void setSkills(UserSkillsResponse skills) {
        this.skills = skills;
    }

    public UserRolesResponse getRoles() {
        return roles;
    }

    public void setRoles(UserRolesResponse roles) {
        this.roles = roles;
    }

    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }

    public PersonalityProfileResponse getPersonalityProfile() {
        return personalityProfile;
    }

    public void setPersonalityProfile(PersonalityProfileResponse personalityProfile) {
        this.personalityProfile = personalityProfile;
    }
}
