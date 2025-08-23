package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
    private String createdAtString; // 서버에서 오는 문자열("2025-08-22T14:57:01")을 받습니다.

    // 서버에서 받은 시간 문자열을 Date 객체로 변환하는 메서드
    public Date getCreatedAt() {
        if (createdAtString == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // 서버 시간이 UTC 기준일 경우
        try {
            return sdf.parse(createdAtString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- Getter ---
    public int getCommentId() { return commentId; }
    public String getContent() { return content; }
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getUserId() { return userId; }
    public Integer getParentCommentId() { return parentCommentId; }
}

/**
 * 대댓글 목록을 포함하는 최상위 댓글 정보를 담는 클래스입니다.
 * GET /comments/post/{id} API의 응답 구조와 일치합니다.
 */
