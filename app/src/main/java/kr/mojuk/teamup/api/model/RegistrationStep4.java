package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 회원가입 4단계 DTO
 */
public class RegistrationStep4 {
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("answers")
    private List<PersonalityAnswer> answers;
    
    public RegistrationStep4(String userId, List<PersonalityAnswer> answers) {
        this.userId = userId;
        this.answers = answers;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public List<PersonalityAnswer> getAnswers() {
        return answers;
    }
    
    public void setAnswers(List<PersonalityAnswer> answers) {
        this.answers = answers;
    }
}
