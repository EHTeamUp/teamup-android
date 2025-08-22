package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class CommentCreate {
    @SerializedName("content")
    private String content;

    @SerializedName("recruitment_post_id")
    private int recruitmentPostId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("parent_comment_id")
    private Integer parentCommentId; // 대댓글 작성 시 사용

    public CommentCreate(String content, int recruitmentPostId, String userId, Integer parentCommentId) {
        this.content = content;
        this.recruitmentPostId = recruitmentPostId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
    }
}
