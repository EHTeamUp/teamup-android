package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PersonalityTestRequest {
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("answers")
    private List<PersonalityAnswer> answers;
    
    public PersonalityTestRequest(String userId, List<PersonalityAnswer> answers) {
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
