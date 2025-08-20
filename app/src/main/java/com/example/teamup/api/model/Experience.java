package com.example.teamup.api.model;

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
    
    @SerializedName("award_name")
    private String awardName;
    
    @SerializedName("description")
    private String description;
    
    public Experience() {
        // 기본 생성자
    }
    
    public Experience(String contestName, String awardDate, String hostOrganization, String awardName, String description) {
        this.contestName = contestName;
        this.awardDate = awardDate;
        this.hostOrganization = hostOrganization;
        this.awardName = awardName;
        this.description = description;
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
    
    public String getAwardName() {
        return awardName;
    }
    
    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
