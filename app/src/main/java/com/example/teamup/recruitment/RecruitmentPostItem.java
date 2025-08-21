package com.example.teamup.recruitment;

import com.example.teamup.api.model.ContestInformation;

public class RecruitmentPostItem {

    // API에서 직접 받는 정보
    private int recruitmentPostId;
    private String title;
    private String userId; // 모집자
    private int recruitmentCount; // 총 모집 인원

    // ==================== 수정된 부분 ====================
    // 추가 API 호출로 가져온 ContestInformation 객체를 통째로 저장합니다.
    private ContestInformation contestInformation;
    private int currentMembers; // 현재 참여 인원은 별도의 API에서 오므로 유지합니다.
    // ======================================================

    // 생성자
    public RecruitmentPostItem(int recruitmentPostId, String title, String userId, int recruitmentCount) {
        this.recruitmentPostId = recruitmentPostId;
        this.title = title;
        this.userId = userId;
        this.recruitmentCount = recruitmentCount;
    }

    // --- Getter ---
    public int getRecruitmentPostId() { return recruitmentPostId; }
    public String getTitle() { return title; }
    public String getUserId() { return userId; }
    public int getRecruitmentCount() { return recruitmentCount; }
    public ContestInformation getContestInformation() { return contestInformation; } // 객체 전체를 반환
    public int getCurrentMembers() { return currentMembers; }

    // --- Setter (Activity에서 추가 정보를 채우기 위함) ---
    public void setContestInformation(ContestInformation contestInformation) { this.contestInformation = contestInformation; }
    public void setCurrentMembers(int currentMembers) { this.currentMembers = currentMembers; }
}