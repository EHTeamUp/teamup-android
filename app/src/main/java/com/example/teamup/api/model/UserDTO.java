package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 사용자 관련 통합 DTO 클래스
 * MySQL users 테이블 구조에 맞춤
 * 로그인 요청과 응답을 모두 처리
 */
public class UserDTO {
    
    // 데이터베이스 필드
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("password_hash")
    private String passwordHash;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("is_deleted")
    private Boolean isDeleted;
    
    // 로그인 요청 필드 (클라이언트에서만 사용)
    @SerializedName("password")
    private String password;
    
    // 로그인 응답 필드
    @SerializedName("access_token")
    private String accessToken;
    
    @SerializedName("token_type")
    private String tokenType;
    
    // 기본 생성자
    public UserDTO() {}
    
    // 로그인 요청용 생성자
    public UserDTO(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
    
    // 로그인 응답용 생성자 (제거됨 - setter 사용 권장)
    // public UserDTO(String accessToken, String tokenType) {
    //     this.accessToken = accessToken;
    //     this.tokenType = tokenType;
    // }
    
    // 전체 사용자 정보 생성자
    public UserDTO(String userId, String name, String passwordHash, String email, Boolean isDeleted) {
        this.userId = userId;
        this.name = name;
        this.passwordHash = passwordHash;
        this.email = email;
        this.isDeleted = isDeleted;
    }
    
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
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    /**
     * 로그인 요청 데이터인지 확인
     */
    public boolean isLoginRequest() {
        return userId != null && password != null && accessToken == null;
    }
    
    /**
     * 로그인 응답 데이터인지 확인
     */
    public boolean isLoginResponse() {
        return accessToken != null && tokenType != null;
    }
    
    /**
     * 사용자가 삭제되지 않았는지 확인
     */
    public boolean isActive() {
        return isDeleted == null || !isDeleted;
    }
    
    /**
     * 사용자 정보가 완전한지 확인
     */
    public boolean isCompleteUserInfo() {
        return userId != null && name != null && email != null;
    }
}
