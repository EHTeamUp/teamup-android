package com.example.teamup.model;

import com.google.gson.annotations.SerializedName;

/**
 * 로그인 응답 DTO 클래스
 */
public class LoginResponse {
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("access_token")
    private String accessToken;
    
    @SerializedName("token_type")
    private String tokenType;
    
    // 기본 생성자
    public LoginResponse() {}
    
    // Getter & Setter
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    /**
     * 전체 토큰 문자열을 반환 (Bearer + 토큰)
     */
    public String getFullToken() {
        if (accessToken != null && tokenType != null) {
            return tokenType + " " + accessToken;
        }
        return null;
    }
}
