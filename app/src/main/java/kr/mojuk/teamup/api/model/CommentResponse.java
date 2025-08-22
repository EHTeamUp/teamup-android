package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class CommentResponse {
    @SerializedName("comment_id")
    private int commentId;

    @SerializedName("content")
    private String content;

    @SerializedName("recruitment_post_id")
    private int recruitmentPostId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("parent_comment_id")
    private Integer parentCommentId; // 최상위 댓글은 null일 수 있으므로 Integer

    @SerializedName("created_at")
    private String createdAt;

    // --- Getter ---
    public int getCommentId() { return commentId; }
    public String getContent() { return content; }
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getUserId() { return userId; }
    public Integer getParentCommentId() { return parentCommentId; }
    public String getCreatedAt() { return createdAt; }
}

/**
 * 대댓글 목록을 포함하는 최상위 댓글 정보를 담는 클래스입니다.
 * GET /comments/post/{id} API의 응답 구조와 일치합니다.
 */
