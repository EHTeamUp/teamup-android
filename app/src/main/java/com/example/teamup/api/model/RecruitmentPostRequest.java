package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

// 'POST /api/v1/recruitments/create' 요청 시 Body에 담길 데이터 모델
public class RecruitmentPostRequest {

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("recruitment_count")
    private int recruitmentCount;

    @SerializedName("contest_id")
    private int contestId;

    @SerializedName("user_id")
    private String userId;

    public RecruitmentPostRequest(String title, String content, int recruitmentCount, int contestId, String userId) {
        this.title = title;
        this.content = content;
        this.recruitmentCount = recruitmentCount;
        this.contestId = contestId;
        this.userId = userId;
    }

}
