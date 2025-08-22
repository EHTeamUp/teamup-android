package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 회원가입 요청 DTO (이메일 인증 포함)
 */
public class UserCreateWithVerification {
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
    
    public UserCreateWithVerification(String userId, String name, String email, String password, String verificationCode) {
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
}
