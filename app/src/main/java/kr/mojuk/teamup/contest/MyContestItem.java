package kr.mojuk.teamup.contest;

public class MyContestItem {
    private final int recruitmentPostId;
    private final String contestTitle;
    private final String teamLeaderId;
    private final int totalMembers;

    public MyContestItem(int recruitmentPostId, String contestTitle, String teamLeaderId, int totalMembers) {
        this.recruitmentPostId = recruitmentPostId;
        this.contestTitle = contestTitle;
        this.teamLeaderId = teamLeaderId;
        this.totalMembers = totalMembers;
    }

    // --- Getter 메서드들 ---
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getContestTitle() { return contestTitle; }
    public String getTeamLeaderId() { return teamLeaderId; }

    // '팀장 외 N명'을 계산하기 위한 메서드
    public int getOtherMembersCount() {
        return Math.max(0, totalMembers - 1);
    }
}

