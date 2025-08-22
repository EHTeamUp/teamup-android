package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 지원자 정보를 담는 모델 클래스
 */
public class Application {
    @SerializedName("application_id")
    private int applicationId;
    
    @SerializedName("recruitment_post_id")
    private int recruitmentPostId;
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("created_at")
    private String createdAt;
    
    // 사용자 정보 (API 응답에 포함될 경우)
    @SerializedName("user_name")
    private String userName;
    
    @SerializedName("user_role")
    private String userRole;
    
    @SerializedName("user_personality")
    private String userPersonality;
    
    @SerializedName("user_experience")
    private String userExperience;
    
    @SerializedName("user_skills")
    private String[] userSkills;

    // 생성자
    public Application() {}

    public Application(int applicationId, int recruitmentPostId, String userId, String message, String status) {
        this.applicationId = applicationId;
        this.recruitmentPostId = recruitmentPostId;
        this.userId = userId;
        this.message = message;
        this.status = status;
    }

    // Getter와 Setter
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getRecruitmentPostId() { return recruitmentPostId; }
    public void setRecruitmentPostId(int recruitmentPostId) { this.recruitmentPostId = recruitmentPostId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getUserPersonality() { return userPersonality; }
    public void setUserPersonality(String userPersonality) { this.userPersonality = userPersonality; }

    public String getUserExperience() { return userExperience; }
    public void setUserExperience(String userExperience) { this.userExperience = userExperience; }

    public String[] getUserSkills() { return userSkills; }
    public void setUserSkills(String[] userSkills) { this.userSkills = userSkills; }
}


