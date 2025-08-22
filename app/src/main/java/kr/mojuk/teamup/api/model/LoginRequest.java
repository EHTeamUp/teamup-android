package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 로그인 요청 DTO 클래스
 */
public class LoginRequest {
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("password")
    private String password;
    
    // 기본 생성자
    public LoginRequest() {}
    
    // 로그인 요청용 생성자
    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
    
    // Getter & Setter
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
