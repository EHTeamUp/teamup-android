package kr.mojuk.teamup.api.model;

import java.util.List;

public class SynergyAnalysisRequest {
    private List<String> user_ids;
    private int filter_id;

    public SynergyAnalysisRequest(List<String> user_ids, int filter_id) {
        this.user_ids = user_ids;
        this.filter_id = filter_id;
    }

    public List<String> getUserIds() {
        return user_ids;
    }

    public void setUserIds(List<String> user_ids) {
        this.user_ids = user_ids;
    }

    public int getFilterId() {
        return filter_id;
    }

    public void setFilterId(int filter_id) {
        this.filter_id = filter_id;
    }
}
