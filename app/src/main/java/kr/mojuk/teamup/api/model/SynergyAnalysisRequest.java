package kr.mojuk.teamup.api.model;

import java.util.List;

public class SynergyAnalysisRequest {
    private List<String> user_ids;

    public SynergyAnalysisRequest(List<String> user_ids) {
        this.user_ids = user_ids;
    }

    public List<String> getUserIds() {
        return user_ids;
    }

    public void setUserIds(List<String> user_ids) {
        this.user_ids = user_ids;
    }
}
