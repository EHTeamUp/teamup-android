package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 모집 게시글 응답 모델
 */
public class RecruitmentPostResponse {
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
    
    @SerializedName("due_date")
    private String dueDate;
    
    @SerializedName("filter_id")
    private Integer filterId;

    public RecruitmentPostResponse(int recruitmentPostId, String title, String content, 
                                 int recruitmentCount, int contestId, String userId, 
                                 String createdAt, String dueDate) {
        this.recruitmentPostId = recruitmentPostId;
        this.title = title;
        this.content = content;
        this.recruitmentCount = recruitmentCount;
        this.contestId = contestId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
    }

    public int getRecruitmentPostId() { return recruitmentPostId; }
    public void setRecruitmentPostId(int recruitmentPostId) { this.recruitmentPostId = recruitmentPostId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getRecruitmentCount() { return recruitmentCount; }
    public void setRecruitmentCount(int recruitmentCount) { this.recruitmentCount = recruitmentCount; }

    public int getContestId() { return contestId; }
    public void setContestId(int contestId) { this.contestId = contestId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    
    public Integer getFilterId() { return filterId; }
    public void setFilterId(Integer filterId) { this.filterId = filterId; }
}
