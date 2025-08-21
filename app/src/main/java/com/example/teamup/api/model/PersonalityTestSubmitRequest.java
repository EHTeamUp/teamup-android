package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PersonalityTestSubmitRequest {
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("answers")
    private List<PersonalityTestAnswer> answers;
    
    public PersonalityTestSubmitRequest(String userId, List<PersonalityTestAnswer> answers) {
        this.userId = userId;
        this.answers = answers;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public List<PersonalityTestAnswer> getAnswers() {
        return answers;
    }
    
    public void setAnswers(List<PersonalityTestAnswer> answers) {
        this.answers = answers;
    }
    
    @Override
    public String toString() {
        return "PersonalityTestSubmitRequest{" +
                "userId='" + userId + '\'' +
                ", answers=" + answers +
                '}';
    }
}
