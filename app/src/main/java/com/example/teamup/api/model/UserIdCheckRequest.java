package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 사용자 ID 중복 검사 요청 DTO
 */
public class UserIdCheckRequest {
    @SerializedName("user_id")
    private String userId;
    
    public UserIdCheckRequest(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
