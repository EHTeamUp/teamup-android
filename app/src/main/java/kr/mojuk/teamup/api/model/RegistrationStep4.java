package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 회원가입 4단계 DTO
 */
public class RegistrationStep4 {
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("skip_personality_test")
    private boolean skipPersonalityTest;
    
    @SerializedName("personality_results")
    private Object personalityResults; // 나중에 구체적인 타입으로 변경
    
    public RegistrationStep4(String userId, boolean skipPersonalityTest, Object personalityResults) {
        this.userId = userId;
        this.skipPersonalityTest = skipPersonalityTest;
        this.personalityResults = personalityResults;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public boolean isSkipPersonalityTest() {
        return skipPersonalityTest;
    }
    
    public void setSkipPersonalityTest(boolean skipPersonalityTest) {
        this.skipPersonalityTest = skipPersonalityTest;
    }
    
    public Object getPersonalityResults() {
        return personalityResults;
    }
    
    public void setPersonalityResults(Object personalityResults) {
        this.personalityResults = personalityResults;
    }
}
