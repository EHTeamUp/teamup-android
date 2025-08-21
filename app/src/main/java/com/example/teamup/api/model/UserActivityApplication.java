package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * UserActivityResponse 내부에 포함된 '내가 참여(수락)된 지원'의 정보를 담는 클래스입니다.
 */
public class UserActivityApplication {
    @SerializedName("application_id")
    private int applicationId;

    @SerializedName("recruitment_post_id")
    private int recruitmentPostId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status; // "pending", "accepted", "rejected"

    // --- Getter 메서드들 ---
    public int getApplicationId() { return applicationId; }
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getUserId() { return userId; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
}
