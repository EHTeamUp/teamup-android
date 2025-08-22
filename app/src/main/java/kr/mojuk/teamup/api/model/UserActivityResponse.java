package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 'GET /api/v1/applications/user/{user_id}/activity' API의 전체 응답을 담는 클래스입니다.
 */
public class UserActivityResponse {

    @SerializedName("written_posts")
    private List<UserActivityPost> writtenPosts;

    @SerializedName("accepted_applications")
    private List<UserActivityApplication> acceptedApplications;

    // --- Getter 메서드들 ---
    public List<UserActivityPost> getWrittenPosts() {
        return writtenPosts;
    }

    public List<UserActivityApplication> getAcceptedApplications() {
        return acceptedApplications;
    }
}

