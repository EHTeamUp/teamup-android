package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 일반적인 API 응답 모델
 */
public class ApiResponse {
    @SerializedName("message")
    private String message;

    public ApiResponse() {
    }

    public ApiResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}