package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 이메일 인증 응답 DTO
 */
public class EmailVerificationResponse {
    @SerializedName("message")
    private String message;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
