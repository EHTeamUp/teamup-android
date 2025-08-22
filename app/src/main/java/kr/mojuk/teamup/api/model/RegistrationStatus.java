package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 회원가입 상태 DTO
 */
public class RegistrationStatus {
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("current_step")
    private int currentStep;
    
    @SerializedName("is_completed")
    private boolean isCompleted;
    
    @SerializedName("completed_steps")
    private List<Integer> completedSteps;
    
    @SerializedName("personality_test_skipped")
    private boolean personalityTestSkipped;
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public int getCurrentStep() {
        return currentStep;
    }
    
    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    public List<Integer> getCompletedSteps() {
        return completedSteps;
    }
    
    public void setCompletedSteps(List<Integer> completedSteps) {
        this.completedSteps = completedSteps;
    }
    
    public boolean isPersonalityTestSkipped() {
        return personalityTestSkipped;
    }
    
    public void setPersonalityTestSkipped(boolean personalityTestSkipped) {
        this.personalityTestSkipped = personalityTestSkipped;
    }
}
