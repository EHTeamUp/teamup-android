package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PersonalityQuestionResponse {
    @SerializedName("questions")
    private List<ApiPersonalityQuestion> questions;
    
    @SerializedName("total_count")
    private int totalCount;
    
    public PersonalityQuestionResponse(List<ApiPersonalityQuestion> questions, int totalCount) {
        this.questions = questions;
        this.totalCount = totalCount;
    }
    
    public List<ApiPersonalityQuestion> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<ApiPersonalityQuestion> questions) {
        this.questions = questions;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
