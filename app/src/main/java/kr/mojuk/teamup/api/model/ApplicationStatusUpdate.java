package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 지원자 수락 요청 모델
 */
public class ApplicationStatusUpdate {
    @SerializedName("recruitment_post_id")
    private int recruitmentPostId;
    
    @SerializedName("user_ids")
    private List<String> userIds;

    public ApplicationStatusUpdate(int recruitmentPostId, List<String> userIds) {
        this.recruitmentPostId = recruitmentPostId;
        this.userIds = userIds;
    }

    public int getRecruitmentPostId() { return recruitmentPostId; }
    public void setRecruitmentPostId(int recruitmentPostId) { this.recruitmentPostId = recruitmentPostId; }

    public List<String> getUserIds() { return userIds; }
    public void setUserIds(List<String> userIds) { this.userIds = userIds; }
}


