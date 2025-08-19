package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 공모전 응답 모델
 */
public class ContestResponse {
    @SerializedName("contest_id")
    private int contestId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("contest_url")
    private String contestUrl;
    
    @SerializedName("start_date")
    private String startDate;
    
    @SerializedName("due_date")
    private String dueDate;
    
    @SerializedName("poster_img_url")
    private String posterImgUrl;

    public ContestResponse(int contestId, String name, String contestUrl, 
                          String startDate, String dueDate, String posterImgUrl) {
        this.contestId = contestId;
        this.name = name;
        this.contestUrl = contestUrl;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.posterImgUrl = posterImgUrl;
    }

    public int getContestId() { return contestId; }
    public void setContestId(int contestId) { this.contestId = contestId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContestUrl() { return contestUrl; }
    public void setContestUrl(String contestUrl) { this.contestUrl = contestUrl; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getPosterImgUrl() { return posterImgUrl; }
    public void setPosterImgUrl(String posterImgUrl) { this.posterImgUrl = posterImgUrl; }
}
