package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class PersonalityAnswer {
    @SerializedName("question_id")
    private int questionId;
    
    @SerializedName("option_id")
    private int optionId;
    
    public PersonalityAnswer(int questionId, int optionId) {
        this.questionId = questionId;
        this.optionId = optionId;
    }
    
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public int getOptionId() {
        return optionId;
    }
    
    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }
}
