package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 공모전 경험 DTO
 */
public class Experience {
    @SerializedName("contest_name")
    private String contestName;
    
    @SerializedName("award_date")
    private String awardDate;
    
    @SerializedName("host_organization")
    private String hostOrganization;
    
    @SerializedName("award_status")
    private int awardStatus;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("filter_id")
    private Integer filterId;
    
    public Experience() {
        // 기본 생성자
    }
    
    public Experience(String contestName, String awardDate, String hostOrganization, int awardStatus, String description) {
        this.contestName = contestName;
        this.awardDate = awardDate;
        this.hostOrganization = hostOrganization;
        this.awardStatus = awardStatus;
        this.description = description;
        this.filterId = 1; // 기본값 설정
    }
    
    public String getContestName() {
        return contestName;
    }
    
    public void setContestName(String contestName) {
        this.contestName = contestName;
    }
    
    public String getAwardDate() {
        return awardDate;
    }
    
    public void setAwardDate(String awardDate) {
        this.awardDate = awardDate;
    }
    
    public String getHostOrganization() {
        return hostOrganization;
    }
    
    public void setHostOrganization(String hostOrganization) {
        this.hostOrganization = hostOrganization;
    }
    
    public int getAwardStatus() {
        return awardStatus;
    }
    
    public void setAwardStatus(int awardStatus) {
        this.awardStatus = awardStatus;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getFilterId() {
        return filterId;
    }
    
    public void setFilterId(Integer filterId) {
        this.filterId = filterId;
    }
    
    @Override
    public String toString() {
        return "Experience{" +
                "contestName='" + contestName + '\'' +
                ", awardDate='" + awardDate + '\'' +
                ", hostOrganization='" + hostOrganization + '\'' +
                ", awardStatus=" + awardStatus +
                ", description='" + description + '\'' +
                ", filterId=" + filterId +
                '}';
    }
}
