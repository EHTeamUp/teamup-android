package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * API 응답 모델
 */
public class ApiResponse {
    @SerializedName("message")
    private String message;

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


