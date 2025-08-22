package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

// 'POST /api/v1/applications/' 요청 시 Body에 담길 데이터 모델
public class ApplicationCreate {
    @SerializedName("recruitment_post_id")
    private int recruitmentPostId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("message")
    private String message;

    public ApplicationCreate(int recruitmentPostId, String userId, String message) {
        this.recruitmentPostId = recruitmentPostId;
        this.userId = userId;
        this.message = message;
    }
}