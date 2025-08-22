package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ContestsListResponse {
    @SerializedName("contests")
    private List<ContestInformation> contests;

    @SerializedName("total_count")
    private int totalCount;

    // Getter
    public List<ContestInformation> getContests() { return contests; }
    public int getTotalCount() { return totalCount; }
}
