package com.example.teamup.recruitment;

import java.util.List;

public class RecruitmentPostDetail {
    // --- 기존 필드 ---
    private final String contestTitle;
    private final int posterResourceId;
    private final String prize;
    private final String dDay;
    private final List<TeamMember> teamMembers;
    private final boolean isRecruiter;

    // ▼▼▼ '수정' 기능을 위해 새로 추가된 필드 ▼▼▼
    private final String title;
    private final String content;
    private final int memberCount;

    public RecruitmentPostDetail(String contestTitle, int posterResourceId, String prize, String dDay,
                                 List<TeamMember> teamMembers, boolean isRecruiter,
                                 String title, String content, int memberCount) {
        this.contestTitle = contestTitle;
        this.posterResourceId = posterResourceId;
        this.prize = prize;
        this.dDay = dDay;
        this.teamMembers = teamMembers;
        this.isRecruiter = isRecruiter;
        this.title = title;
        this.content = content;
        this.memberCount = memberCount;
    }

    // --- Getter 메서드들 ---
    public String getContestTitle() { return contestTitle; }
    public int getPosterResourceId() { return posterResourceId; }
    public String getPrize() { return prize; }
    public String getdDay() { return dDay; }
    public List<TeamMember> getTeamMembers() { return teamMembers; }
    public boolean isRecruiter() { return isRecruiter; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getMemberCount() { return memberCount; }
}