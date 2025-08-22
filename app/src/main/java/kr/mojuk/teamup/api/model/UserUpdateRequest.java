package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 사용자 정보 업데이트 요청 DTO
 */
public class UserUpdateRequest {
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("current_password")
    private String currentPassword;
    
    @SerializedName("new_password")
    private String newPassword;
    
    public UserUpdateRequest() {}
    
    public UserUpdateRequest(String name, String currentPassword, String newPassword) {
        this.name = name;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
