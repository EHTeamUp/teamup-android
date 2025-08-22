package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 회원가입 1단계 DTO
 */
public class RegistrationStep1 {
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("verification_code")
    private String verificationCode;
    
    public RegistrationStep1(String userId, String name, String email, String password, String verificationCode) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.verificationCode = verificationCode;
    }
    
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
    @Override
    public String toString() {
        return "RegistrationStep1{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + (password != null ? "***" : "null") + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                '}';
    }
}
