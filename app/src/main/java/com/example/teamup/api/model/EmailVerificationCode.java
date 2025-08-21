package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 이메일 인증번호 검증 DTO
 */
public class EmailVerificationCode {
    @SerializedName("email")
    private String email;
    
    @SerializedName("verification_code")
    private String verificationCode;
    
    public EmailVerificationCode(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
    
    @Override
    public String toString() {
        return "EmailVerificationCode{" +
                "email='" + email + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                '}';
    }
}
