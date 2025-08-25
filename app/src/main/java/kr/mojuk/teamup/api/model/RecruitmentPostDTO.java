package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    // --- 추가: 공모전 이름을 받을 필드 ---
    @SerializedName("contest_name")
    private String contestName;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("accepted_count")
    private int acceptedCount;

    @SerializedName("filter_id")
    private int filterId;

    // --- Getter 메서드들 ---
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getRecruitmentCount() { return recruitmentCount; }
    public int getContestId() { return contestId; }
    public String getContestName() { return contestName; } // --- 추가: Getter ---
    public String getUserId() { return userId; }
    public String getCreatedAt() { return createdAt; }
    public String getDueDate() { return dueDate; }
    public int getAcceptedCount() { return acceptedCount; }
    public int getFilterId(){ return filterId; }

    public LocalDate getDueDateAsDate() {
        if (dueDate == null || dueDate.isEmpty()) {
            return null;
        }
        try {
            // "yyyy-MM-dd" 형식의 문자열을 LocalDate로 파싱합니다.
            return LocalDate.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            // 파싱 중 오류가 발생하면 null을 반환합니다.
            return null;
        }
    }

    // ▼▼▼ 수정된 부분 ▼▼▼
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
                Objects.equals(content, that.content) &&
                Objects.equals(contestName, that.contestName) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(createdAt, that.createdAt) && // createdAt 비교 추가
                Objects.equals(dueDate, that.dueDate);
    }

    @Override
    public int hashCode() {
        // createdAt 해시코드 추가
        return Objects.hash(recruitmentPostId, title, content, recruitmentCount, contestId, contestName, userId, createdAt, dueDate, acceptedCount);
    }
    // ▲▲▲ 수정된 부분 ▲▲▲
}