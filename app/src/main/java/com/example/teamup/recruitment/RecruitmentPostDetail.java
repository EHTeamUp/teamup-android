package com.example.teamup.recruitment;

import java.util.List;

public class RecruitmentPostDetail {
    private final String contestTitle;
    private final int posterResourceId;
    private final String prize;
    private final String dDay;
    private final List<TeamMember> teamMembers;
    private final boolean isRecruiter;

    public RecruitmentPostDetail(String contestTitle, int posterResourceId, String prize, String dDay, List<TeamMember> teamMembers, boolean isRecruiter) {
        this.contestTitle = contestTitle;
        this.posterResourceId = posterResourceId;
        this.prize = prize;
        this.dDay = dDay;
        this.teamMembers = teamMembers;
        this.isRecruiter = isRecruiter;
    }

    // Getter 메서드들
    public String getContestTitle() { return contestTitle; }
    public int getPosterResourceId() { return posterResourceId; }
    public String getPrize() { return prize; }
    public String getdDay() { return dDay; }
    public List<TeamMember> getTeamMembers() { return teamMembers; }
    public boolean isRecruiter() { return isRecruiter; } // ◀◀◀ Getter 추가
}