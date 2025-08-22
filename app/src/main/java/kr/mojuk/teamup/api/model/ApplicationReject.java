package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 지원자 거절 요청 모델
 */
public class ApplicationReject {
    @SerializedName("recruitment_post_id")
    private int recruitmentPostId;
    
    @SerializedName("user_id")
    private String userId;

    public ApplicationReject(int recruitmentPostId, String userId) {
        this.recruitmentPostId = recruitmentPostId;
        this.userId = userId;
    }

    public int getRecruitmentPostId() { return recruitmentPostId; }
    public void setRecruitmentPostId(int recruitmentPostId) { this.recruitmentPostId = recruitmentPostId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}


