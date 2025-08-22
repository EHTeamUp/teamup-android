package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class PersonalityTestAnswer {
    @SerializedName("question_id")
    private Integer questionId;
    
    @SerializedName("option_id")
    private Integer optionId;
    
    public PersonalityTestAnswer(Integer questionId, Integer optionId) {
        this.questionId = questionId;
        this.optionId = optionId;
    }
    
    public Integer getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }
    
    public Integer getOptionId() {
        return optionId;
    }
    
    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }
    
    @Override
    public String toString() {
        return "PersonalityTestAnswer{" +
                "questionId=" + questionId +
                ", optionId=" + optionId +
                '}';
    }
}
