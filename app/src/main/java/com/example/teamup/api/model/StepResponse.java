package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 단계 응답 DTO
 */
public class StepResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("current_step")
    private int currentStep;
    
    @SerializedName("next_step")
    private Integer nextStep;
    
    @SerializedName("is_completed")
    private boolean isCompleted;
    
    @SerializedName("can_skip_personality")
    private boolean canSkipPersonality;
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getCurrentStep() {
        return currentStep;
    }
    
    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }
    
    public Integer getNextStep() {
        return nextStep;
    }
    
    public void setNextStep(Integer nextStep) {
        this.nextStep = nextStep;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    public boolean isCanSkipPersonality() {
        return canSkipPersonality;
    }
    
    public void setCanSkipPersonality(boolean canSkipPersonality) {
        this.canSkipPersonality = canSkipPersonality;
    }
}
