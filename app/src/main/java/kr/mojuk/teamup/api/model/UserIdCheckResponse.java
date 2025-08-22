package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 사용자 ID 중복 검사 응답 DTO
 */
public class UserIdCheckResponse {
    @SerializedName("available")
    private boolean available;
    
    @SerializedName("message")
    private String message;
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
