package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

// 'GET /.../applications/...' API의 응답 항목을 담는 데이터 모델
public class ApplicationResponse {

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

    public int getApplicationId() { return applicationId; }
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getUserId() { return userId; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
}

