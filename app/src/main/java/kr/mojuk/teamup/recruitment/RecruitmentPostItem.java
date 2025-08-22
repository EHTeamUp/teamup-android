package kr.mojuk.teamup.recruitment;

import kr.mojuk.teamup.api.model.ContestInformation;

public class RecruitmentPostItem {

    // API에서 직접 받는 정보
    private int recruitmentPostId;
    private String title;
    private String userId; // 모집자
    private int recruitmentCount; // 총 모집 인원
    private int contestId;


    // 추가 API 호출로 가져온 ContestInformation 객체를 통째로 저장합니다.
    private ContestInformation contestInformation;

    // 현재 참여 인원은 별도의 API에서 오므로 유지합니다.
    private int currentMembers;

    // 생성자
    public RecruitmentPostItem(int recruitmentPostId, String title, String userId, int recruitmentCount, int contestId) {
        this.recruitmentPostId = recruitmentPostId;
        this.title = title;
        this.userId = userId;
        this.recruitmentCount = recruitmentCount;
        this.contestId = contestId; // 추가
    }

    // --- Getter ---
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getTitle() { return title; }
    public String getUserId() { return userId; }
    public int getContestId() { return contestId; }
    public int getRecruitmentCount() { return recruitmentCount; }
    public ContestInformation getContestInformation() { return contestInformation; }
    public int getCurrentMembers() { return currentMembers; }

    // --- Setter (Activity에서 추가 정보를 채우기 위함) ---
    public void setContestInformation(ContestInformation contestInformation) { this.contestInformation = contestInformation; }
    public void setCurrentMembers(int currentMembers) { this.currentMembers = currentMembers; }
}