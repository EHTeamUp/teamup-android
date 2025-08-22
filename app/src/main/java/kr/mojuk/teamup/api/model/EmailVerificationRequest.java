package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 이메일 인증 요청 DTO
 */
public class EmailVerificationRequest {
    @SerializedName("email")
    private String email;
    
    public EmailVerificationRequest(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
