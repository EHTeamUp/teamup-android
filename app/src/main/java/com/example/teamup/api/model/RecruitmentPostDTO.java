package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class RecruitmentPostDTO {

    @SerializedName("recruitment_post_id")
    private int recruitmentPostId;

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

    @SerializedName("created_at")
    private String createdAt;

    // --- 추가된 필드 ---
    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("accepted_count")
    private int acceptedCount;

    // --- Getter 메서드들 ---
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getRecruitmentCount() { return recruitmentCount; }
    public int getContestId() { return contestId; }
    public String getUserId() { return userId; }
    public String getCreatedAt() { return createdAt; }

    // --- 추가된 Getter ---
    public String getDueDate() { return dueDate; }
    public int getAcceptedCount() { return acceptedCount; }

    // ListAdapter의 효율적인 업데이트를 위해 equals와 hashCode를 오버라이드합니다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecruitmentPostDTO that = (RecruitmentPostDTO) o;
        return recruitmentPostId == that.recruitmentPostId &&
                recruitmentCount == that.recruitmentCount &&
                contestId == that.contestId &&
                acceptedCount == that.acceptedCount &&
                Objects.equals(title, that.title) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(dueDate, that.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recruitmentPostId, title, recruitmentCount, contestId, userId, dueDate, acceptedCount);
    }
}