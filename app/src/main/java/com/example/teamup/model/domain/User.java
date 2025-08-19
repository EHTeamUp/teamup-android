package com.example.teamup.model.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 사용자 도메인 모델 클래스
 * MySQL users 테이블 구조에 맞춤
 */
public class User {
    
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
    
    // 기본 생성자
    public User() {}
    
    // 전체 사용자 정보 생성자
    public User(String userId, String name, String passwordHash, String email, Boolean isDeleted) {
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
