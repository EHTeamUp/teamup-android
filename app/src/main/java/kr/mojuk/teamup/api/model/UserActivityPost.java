package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 2. "written_posts" 배열 안의 개별 객체를 위한 DTO 클래스
 */

public class UserActivityPost {
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

    // --- Getter 메서드들 ---
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getRecruitmentCount() { return recruitmentCount; }
    public int getContestId() { return contestId; }
    public String getUserId() { return userId; }
    public String getCreatedAt() { return createdAt; }
}